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
#### 참고
https://stackoverflow.com/questions/41767860/spring-transactional-with-synchronized-keyword-doesnt-work
### Database 사용하여 개선
#### pessimistic lock
pessimisticLock 은 충돌이 발생한다고 가정하고 실제 DB에 걸어서 처리한다. 실제 exclusive lock 을 거는 행위이다. 
#### optimistic lock
optimisticLock 은 데이터 갱신시 충돌이 발생하지 않을 것을 가정하는 것이다.  
version 을 트랜잭션 내부에 저장하고 업데이트 직전에 확인여 버전을 확인하는 방식으로 충돌을 방지한다.  
만약 동시 접근일 경우 개발자가 다시 시도하도록 파사드 패턴을 구현한다.  
데이터베이스에 직접 lock을 걸지 않기 때문에 성능상 이점이 있다. 하지만, 데이터베이스의 충돌이 자주 발생한다면 데이터베이스에 lock을 거는 pessimistic lock이 더 좋다. 
#### named lock
namedLock 은 공통된 저장소에서 자원의 사용을 확인하고 싶을 때, 즉 분산락 환경에서 사용된다.  
한 트랜잭션 내에서 특정 메타데이터(문자열)에 대하여 Lock 을 걸고 이를 트랜잭션 종료시 해제 하는 방식으로 사용한다.
#### 참고  
https://techblog.woowahan.com/2631/ (우아한 형제들 기술 블로그, mysql 을 사용한 분산락 처리)  
https://hyperconnect.github.io/2019/11/15/redis-distributed-lock-1.html (하이퍼커넥트 기술블로그, redis를 사용한 분산락)  

### Redis 사용 개선
#### Lettuce
#### Redisson



