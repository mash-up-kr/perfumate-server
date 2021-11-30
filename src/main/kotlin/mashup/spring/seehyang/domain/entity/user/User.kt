package mashup.spring.seehyang.domain.entity.user

import mashup.spring.seehyang.controller.api.dto.user.RegisterUserDetailRequest
import mashup.spring.seehyang.controller.api.dto.user.UserDto
import mashup.spring.seehyang.controller.api.response.SeehyangStatus
import mashup.spring.seehyang.domain.entity.BaseTimeEntity
import mashup.spring.seehyang.domain.entity.Image
import mashup.spring.seehyang.domain.entity.community.Comment
import mashup.spring.seehyang.domain.entity.community.StoryLike
import mashup.spring.seehyang.domain.entity.community.Story
import mashup.spring.seehyang.domain.entity.perfume.Gender
import mashup.spring.seehyang.exception.BadRequestException
import java.util.regex.Pattern
import javax.persistence.*


@Entity
class User(

    id: Long? = null,

    email: String,

    oAuthType: OAuthType,

) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = id
        get() = if(isActivated) field else null

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null
        get() = if(isActivated) field else null
        protected set

    var age: Short? = null
        get() = if(isActivated) field else null
        protected set

    var nickname: String? = null
        get() = if(isActivated) field else null
        protected set

    @Column(unique = true)
    val email: String? = email
        get() = if(isActivated) field else null

    @Enumerated(EnumType.STRING)
    var oAuthType: OAuthType? = oAuthType
        get() = if(isActivated) field else null
        protected set

    var isActivated: Boolean = true
        protected set

    /**
     * =============== PK ===================
     */

    @OneToOne
    @JoinColumn(name = "image_id")
    var profileImage: Image? = null
        protected set

    @OneToMany(mappedBy = "user")
    private val stories: MutableList<Story> = mutableListOf()

    //TODO: 좋아요한 게시물 보기 기능 추가 시 사용
    @OneToMany(mappedBy = "user")
    private val storyLikes: MutableSet<StoryLike> = mutableSetOf()


    @OneToMany(mappedBy = "user")
    private val comments: MutableList<Comment> = mutableListOf()




    companion object {
        fun empty(): User =
            User(
                email = "",
                oAuthType = OAuthType.UNKNOWN
            )
        fun isLogin(userDto: UserDto):Boolean{
            return userDto.email.isNullOrBlank().not() && userDto.oAuthType != OAuthType.UNKNOWN && userDto.id != null
        }
    }

    fun addUserInfo(age:Int, gender:Gender, nickname: String) {
        changeAge(age)
        changeGender(gender)
        changeNickname(nickname)
    }

    fun changeAge(age: Int){
        validateAge(age)
        this.age = age.toShort()
    }

    fun changeGender(gender: Gender){
        this.gender = gender
    }

    fun changeNickname(nickname: String){
        validateNickname(nickname)
        this.nickname = nickname
    }

    fun disableUser(){
        this.isActivated = false
    }

    fun replaceProfileImage(image: Image){
        this.profileImage = image
    }

    fun isLogin(): Boolean =
        this.email.isNullOrBlank().not() && this.oAuthType != OAuthType.UNKNOWN && id != null


    /**
     * ================== Private Methods ==================
     */

    private fun validateNickname(nickname: String) {
        if(Pattern.matches("^[A-Za-z0-9가-힣]+\$", nickname).not() || nickname.length > 12){
            throw BadRequestException(SeehyangStatus.INVALID_NICKNAME)
        }
    }

    private fun validateAge(age: Int) {
        if(age < 0 || age > 100) throw BadRequestException(SeehyangStatus.INVALID_AGE)
    }


}