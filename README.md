# 자바 동시성 이슈 개선 연습 - 재고 관련

### 기본 재고감소 로직
Service 에서 접근하여 단순하게 엔티티의 재고를 감소 시켜본다. 이후 repository를 사용하여 재고 갱신.

**문제점**

thread 를 사용하여 동시에 메소드를 실행하면 데이터베이스에 반영되기전에 필드값을 동시에 읽을 경우 재고감소를 예상대로 실행하지 못한다.
### Synchronized 사용하여 개선
Synchronized 는 자바에서 여러 스레드가 접근하여 메소드를 실행할 때, 메소드가 순차적으로 실행을 함을 보장하는 키워드이다.

@Transactional 은 메소드나 클래스에 사용되어 트랜잭션의 commit/rollback을 보조하는 프록시 객체(JDK 동적 프록시, AOP)를 생성하는 키워드이다.

하지만 위의 두 키워드가 같이 사용될 떄는 @Transactional 이 별도의 프록시 객체를 생성하고 이후에 synchronized가 적용되기 때문에 올바르게 작동하지 않는다.
따라서 동시성 문제를 synchronized로 해결하고 싶을 때는 synchronized를 먼저 사용하고 이후 내부적으로 transactional 을 사용하는 방향으로 해야한다.

**문제점**

단일 서버의 경우에는 문제가 없지만, synchronized는 단일 프로세스에 대해서 멀티 쓰레드에 대한 순차적 접근만을 허용하므로 여러 서버에 요청을 처리한다면 여전히 동시성 이슈가 발생한다.

참고 : https://stackoverflow.com/questions/41767860/spring-transactional-with-synchronized-keyword-doesnt-work
### Database 사용하여 개선

