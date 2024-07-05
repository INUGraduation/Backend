package com.example.inu.domain.user.repositories;



import com.example.inu.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

}

/*
 * extends 키워드는 클래스가 다른 클래스를 상속하거나 인터페이스가 다른 인터페이스를 상속할 때 사용됩니다.
 * 반면에 implements 키워드는 클래스가 인터페이스를 구현할 때 사용됩니다. 인터페이스 간의 상속과 클래스가 인터페이스를 구현하는 것은 서로 다른 개
 * */

/*
*
Java에서 Optional 클래스는 값이 없을 수도 있는 객체를 캡슐화하는 데 사용됩니다. Optional은 주로 데이터 접근 계층에서 사용되어 데이터가 존재하지 않을 때 null 대신 안전한 방식으로 값을 처리할 수 있게 해 줍니다.

UserRepository의 findByEmail 메소드는 Optional<User>을 반환하도록 설계되어 있기 때문에, 이를 User 객체로 직접 사용하려 할 때 "Incompatible types" 오류가 발생합니다.
* 이 오류를 해결하기 위해 Optional 객체에서 값을 추출해야 합니다.

아래는 getAuthentication 메소드에서 Optional<User>을 처리하는 방법을 수정한 코드입니다.
* Optional의 orElse 또는 orElseThrow 메소드를 사용하여 값이 존재하지 않을 때 적절히 처리할 수 있습니다.*/
