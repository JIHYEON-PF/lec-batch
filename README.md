### Lecture/ Spring Boot Batch By Fastcampus

# Batch

### 강의 목차
1. 요구사항 이해하기
2. Batch 설계
3. 기능 구현
4. 테스트 및 회고

### Batch?
- 데이터를 실시간으로 처리하는것이 아닌 일괄적으로 처리하는 행위
- 스프링에서 제공하는 특성 그대로 사용 가능
- != 스케쥴러
- 스프링 배치는 잡을 관리는 하지만, 실행시키는 주체는 아니다.
- Application: 배치 처리를 위한 모든 사용자 코드 및 구성
- Batch Core: Job, Step, JobLauncher, JonParameter
- Batch Infrastructure: File, DB

### Batch 프로젝트 설계
### Batch 구조 설계
- Step : 배치 처리를 정의하고 제어하는 독립된 작업의 단위
    - Taskletv Step: 간단히 정의된 하나의 작업 처리
        - 회원의 상태변경, 레거시 데이터의 제거 등 단일 처리 작업
        - ![Clipboard_2023-07-22-15-25-36](https://github.com/JIHYEON-PF/lec-batch/assets/108737977/8589247b-bd50-4655-adf9-1cd5e19f0e17)
    - Chunk-orientedStep: 한번에 하나씩 데이터를 읽고 Chunk를 만든 후 chunk 단위로 트랜잭션을 처리
    - ![Clipboard_2023-07-22-15-26-02](https://github.com/JIHYEON-PF/lec-batch/assets/108737977/629471ea-a977-4a16-81ca-d1c2a0a0013b)
    - ![Clipboard_2023-07-22-15-27-05](https://github.com/JIHYEON-PF/lec-batch/assets/108737977/98d670c8-846f-43f5-a6c1-3ef1e356c2dd)
    - ItemProcessor: 선택적 요소로 필요시에만 사용해도 무방하다
- Job : 처음부터 끝까지 독립적으로 실행할 수 있으며 고유하고 순서가 지정된 여러 스탭들의 모음
    - ![Clipboard_2023-07-22-15-30-06](https://github.com/JIHYEON-PF/lec-batch/assets/108737977/783acfa3-93ba-4489-a99f-709988703fa1)
    - ![Clipboard_2023-07-22-15-31-41](https://github.com/JIHYEON-PF/lec-batch/assets/108737977/c1cf833a-84ff-42d8-9d00-8a6f93931636)

- ItemReader
    - read: chunk 기반의 배치 과정에서 사용할 하나의 데이터 로우를 조회
    - Cursor VS Paging
        - Cursur
            - DB <-- 1 row --> Cursur ItemReader
            - 표준 java.sql.ResultSet
            - Database와 Connection을 맺은 후 한 번에 하나씩 레코드를 Streaming하여 다음 레코드로 진행한다. (Cursur를 움직인다.)
            - 페이징 보다 성능이 빠를 수 있으나, 수행시간이 오래걸릴 경우 connection이 종료될 수 있음
            - JdbcCursurItemReader
            - HibernateCursorItemReader
            - **JpaCursorItemReader**
        - Paging
            - DB <-- n row --> Paging ItemReader
            - Page라고 부르는 Chunk 크기만큼의 레코드를 가져온다. (PageSize == ChunkSize)
            - 각 페이지의 쿼리를 실행할 때마다 동일한 레코드 정렬 순서를 보장하려면, 정렬 조건이 필요하다.
            - 커서보다 속도가 느릴 수 있지만, 하나의 페이징마다 커넥션을 연결 및 종료하기 때문에 보다 안정적으로 사용할 수 있음
            - JdbcPagingItemReader
            - HibernatePagingItemReader
            - **JpaPagingItemReader**

- ItemWriter
    - Chunk단위로 DB에 데이터를 작성한다.
    - write의 경우 파라미터로 List 컬렉션을 받아서 처리한다.

- Tasklet Repeat와 RepeatStatus
    - Spring Batch는 Step과 그 하위 Chunk의 반복작업이며, Repeat 정책을 따른다.
    - CONTINUABLE
        - 처리를 계속할 수 있음
        - SpringBatch에게 해당 Tasklet을 다시 실행하도록 정의
    - FINISHED
        - 처리가 완료되었음
        - 처리의 성공 여부에 관계없이 Tasklet의 처리를 완료하고 다음 처리를 진행

- AsyncItemProcessor : ItemProcessor에 별도의 Thread가 할당되어 처리하는 방식
    - 복잡한 계산 등으로 인해 ItemProcessor의 작업 수행 시간이 오래걸릴 때, AsyncItemProccessor 및 AsyncItemWriter를 사용하면 성능이 개선된다.
    - 동작 방식
        1. 특정 아이템이 AsyncItemProcessor에 전달될 때 delegate 호출은 새 Thread에서 호출
        2. ItemProcessor의 결과로 반환된 Future는 AsyncItemWriter로 전달됨
        3. AsyncItemWriter는 Future를 처리한 후 결과를 ItemWriter로 전달
    4. ItemWriter에서는 Future 안에 있는 아이템을 꺼내서 일괄 처리
        - Processor에서 작업중인 비동기 실행의 결과값들은 모두 받아올 때까지 대기
        5. AsyncItemProcessor와 AsyncItemWriter를 같이 써야 Future를 처리해줄 수 있다.

- BeanScope
    - @JobScope, @StepScope
        - SpringBean의 기본 Scope는 Singleton
        - Bean의 생성 시점을 지정된 Scope가 명시된 method가 실행되는 시점으로 지연
            - JobScope : Job이 실핼될 때 생성되고 끝날 때 삭제
            - StepScope : Step이 실행될 때 생성되고 끝날 때 삭제
        - JobParameter를 method 실행하는 시점까지 지연시켜 할당할 수 있다.
        - 동일한 Component를 병렬로 처리할 때 안전할 수 있다. 
