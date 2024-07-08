package com.example.inu.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY="auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;
    //빈이 생성이 되고 secret값주입
    //@Value 어노테이션을 사용하여 application.properties에서 설정된 jwt.secret과 jwt.token-validity-in-seconds 값을 받아옴.
    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
                         @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds){
        this.secret=secret;
        this.tokenValidityInMilliseconds=tokenValidityInMilliseconds*1000;
        this.refreshTokenValidityInMilliseconds= refreshTokenValidityInMilliseconds*1000;
    }
    //주입받은 secret값을 Base64 decode해서 key 변수에 할당
    public void afterPropertiesSet(){
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createRefreshToken(String useremail) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(useremail)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    //Authentication객체의 권한정보를 이용해서 토큰을 생성하는 createToken메서드
    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now= (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds); //application.properties에 설정했던 만료시간 설정

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY,authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();//jwt토큰 생성해서 return
    }

    //이번에는 역으로 token에 담겨있는 정보를 디코딩해 Authentication객체를 리턴하는 메서드
    public Authentication getAuthentication(String token){
        Claims claims =Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();//토큰을 이용해서 claim을 만들고

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());//claim에서 권한정보를 빼내서 권한정보들을 이용해서 User 객체를 만들어주고

        User principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal,token,authorities);

    }
    //토큰을 매개변수로 받아서 token유효성 검증을 수행하는 validateToken메서드
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    //토큰을 파싱해보고 발생하는 익셉션들을 캐치, 문제가있으면 false, 정상이면 true
    // JWT (JSON Web Token)를 파싱(parsing)한다는 것은, 토큰의 문자열 형태를 분석하여 그 안에 담긴 정보를 추출하고 구조적으로 이해하는 과정
    //즉 header/payload/signature 분석한다는 뜻

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

/*
* Authentication인터페이스에대한 설명
* Spring Security에서 `Authentication` 인터페이스는 로그인이 성공한 사용자의 인증 상태를 표현하는 데 사용됩니다.
*  이 인터페이스는 사용자의 신원 정보와 권한을 포함하고, 인증된 사용자의 세부 정보를 저장하는 역할을 합니다.

`Authentication` 객체는 다음과 같은 주요 정보를 포함합니다:

1. **Principal**: 사용자의 식별 정보, 일반적으로 사용자 이름이나 사용자 객체가 여기에 해당합니다.
2. **Credentials**: 사용자의 자격 증명 정보, 일반적으로 비밀번호나 특정 키 값입니다. 인증 후에는 보안을 위해 자주 삭제됩니다.
3. **Authorities**: 사용자에게 부여된 권한 목록을 나타냅니다. 이 권한은 사용자가 시스템 내에서 수행할 수 있는 작업을 결정합니다.
4. **Details**: 추가적인 사용자 정보를 저장할 수 있으며, 요청에 관련된 메타데이터 등을 포함할 수 있습니다.
5. **Authenticated**: 이 속성은 사용자의 인증 여부를 나타냅니다. 인증이 완료되면 `true`로 설정됩니다.

`Authentication` 객체는 인증 프로세스의 일부로 생성되며, 일반적으로 로그인 과정에서 사용자의 자격 증명을 검증한 후에 생성됩니다.
* 인증이 성공하면, 생성된 `Authentication` 객체는 Spring Security의 `SecurityContextHolder`에 저장되어 애플리케이션의 다른 부분에서 사용자의 인증 상태를 조회하고 확인하는 데 사용됩니다.
* 이를 통해 시스템은 현재 사용자의 인증 상태에 따라 접근 제어 결정을 내릴 수 있습니다.
* */

/*
* tokenprovider메서드 설명
* JWT 토큰 생성:
Jwts.builder()
.setSubject(authentication.getName()) : 토큰의 주체를 설정합니다. 일반적으로 사용자의 이름이나 ID가 사용됩니다.
.claim(AUTHORITIES_KEY, authorities) : 사용자의 권한 정보를 토큰에 포함합니다.
.signWith(key, SignatureAlgorithm.HS512) : 지정된 키(key)와 HS512 알고리즘을 사용해 토큰을 서명합니다.
.setExpiration(validity) : 위에서 계산한 만료 시간을 토큰에 설정합니다.
.compact() : 위의 설정을 모두 포함하는 JWT 토큰을 생성하고 문자열 형태로 반환합니다.
* */


/*
getAuthentication메서드 설명
* getAuthentication 메서드는 JWT를 디코딩하고, 그 안에서 사용자 ID와 권한 정보를 추출하여 Spring Security의 Authentication 객체를 생성하는 역할을 합니다.
*  이 메서드는 JWT의 유효성 검증 및 파싱을 통해 Claims 객체를 얻어내고, 이 객체에서 사용자의 식별자(subject)와 권한(authorities)를 추출합니다.
토큰 파싱: Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)을 통해 주어진 JWT를 파싱합니다. 이 과정에서 토큰의 서명을 검증하고, JWT 구조의 유효성을 확인합니다.
Claims 추출: 파싱된 토큰에서 getBody() 메서드를 호출하여 Claims 객체를 얻습니다. 이 객체는 토큰에 포함된 정보들, 즉 페이로드에 접근할 수 있게 해줍니다.
사용자 식별자 추출: claims.getSubject() 메서드를 통해 토큰에 저장된 사용자의 ID(일반적으로 사용자의 식별자로 사용됨)를 추출합니다.
권한 정보 추출: 토큰의 auth 클레임에서 사용자에게 할당된 권한을 추출합니다.
* 이 권한 정보는 콤마로 구분된 문자열 형태로 저장되어 있으며, 이를 분리하고 SimpleGrantedAuthority 객체 리스트로 변환합니다.
Authentication 객체 생성: 추출한 사용자 ID와 권한 정보를 바탕으로 User 객체를 생성하고, 이를 사용하여 UsernamePasswordAuthenticationToken 객체를 반환합니다.
* 이 객체는 Spring Security에서 사용자의 인증 정보를 나타내는 Authentication 인터페이스의 구현체입니다.
따라서, getAuthentication 메서드는 JWT에서 사용자 식별자를 디코딩하고 추출하는 핵심적인 역할을 수행합니다.
* 이 메서드를 통해 얻어진 Authentication 객체는 사용자가 인증된 상태를 나타내며, 시스템 내에서 사용자의 인증 상태를 관리하는 데 필수적입니다.
* */


/*
*
* 여기서 Access Token과 Refresh Token을 생성하는 방식의 차이는 각 토큰의 목적과 사용 방식에 기인합니다.

### Access Token 생성 시 `Authentication` 객체 사용 이유:
Access Token은 사용자의 인증 상태를 나타내며, 해당 사용자가 시스템 내에서 수행할 수 있는 작업의 범위(즉, 권한)를 포함합니다. `Authentication` 객체는 다음을 포함하고 있습니다:
- **Principal**: 사용자의 식별 정보
- **Credentials**: 일반적으로 사용자의 비밀번호나 기타 인증 정보
- **Authorities**: 사용자에게 부여된 권한을 나타내는 역할과 권한 목록

이 정보를 이용하여 Access Token을 생성할 때, 사용자의 권한을 Token 내에 인코딩할 수 있습니다 (`claim(AUTHORITIES_KEY, authorities)`를 통해). 이를 통해, 토큰을 수신하는 서버 측 시스템이 사용자가 누구인지, 어떤 작업을 수행할 수 있는지 판단할 수 있습니다.

### Refresh Token 생성 시 `username`만 사용하는 이유:
반면, Refresh Token은 사용자의 권한 변경이 반영될 필요가 없이, 단지 새로운 Access Token을 발급받기 위한 인증 수단으로만 사용됩니다. 따라서 Refresh Token에는 사용자를 식별할 수 있는 최소한의 정보만 포함되면 충분합니다. 이 경우 `username`만 있어도 사용자가 누구인지 식별할 수 있기 때문에, 추가적인 권한 정보는 포함하지 않습니다.

Refresh Token은 주로 Access Token이 만료되었을 때 새로운 Access Token을 발급받는 데 사용되며, 이 과정에서 새로운 인증 요청이 들어오면 `Authentication` 객체가 다시 생성되고, 이를 통해 새로운 권한 정보가 반영된 Access Token이 발급됩니다.

### 설계적 고려:
- **보안**: Refresh Token에는 보안적으로 민감한 정보(예: 권한 정보)를 최소화하여 포함시키는 것이 좋습니다. 만약 Refresh Token이 탈취되어도, 공격자가 사용자의 권한에 대한 정보를 얻거나 변경할 수 없습니다.
- **성능**: Refresh Token 생성 시 권한 정보를 처리하고 토큰에 인코딩하는 과정은 시스템 자원을 추가적으로 소모합니다. 권한 정보가 필요하지 않은 경우 이를 생략함으로써 효율성을 높일 수 있습니다.

이러한 차이는 JWT를 효율적으로 사용하면서도 보안을 유지하기 위한 설계 전략의 일부로 볼 수 있습니다.*/