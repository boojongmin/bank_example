# 카프카를 이용한 BANK 예제

## 들어가기전
카프카를 이용해서 사용자 로그를 수집한다. 
- Producer: 비지니스 코드 동작 후 로그를 발행
- Consumer: 로그가 발행되면 Inmemory DB에 저장.
- Webserver: http 호출시 Inmemory DB에 저장된 값을 반환 

## 개발에 사용한 도구
- docker, docker-compose, kafka 설치
- java 8, kotlin
- maven 3
- intellij

## 실행방법
```shell
mkdir bank-example
cd bank-example
git clone https://github.com/wurstmeister/kafka-docker.git
cd kafka-docker
ifconfig # ip 확인
vi docker-compose.yml  #  KAFKA_ADVERTISED_HOST_NAME: {ip 수정}
sudo docker-compose up -d
cd ..
git clone https://github.com/boojongmin/bank_example.git
cd bank_example
mvn package
# consumer 실행
nohup java -jar consumer/target/consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar &
# tail -f nohup.out
# producer 실행
java -jar producer/target/producer-0.0.1-SNAPSHOT-jar-with-dependencies.jar

# 전체 조회 
curl localhost:467/member

# 상세조회(number 필드로 조회) 
curl localhost:4567/member/13

kill $(pgrep -a java | grep consumer | awk '{print $1}')
```

### line coverage 확인(jacoco maven plugin 사용)
```
mvn test
firefox producer/target/site/jacoco/index.html 
firefox consumer/target/site/jacoco/index.html 
```

bank <- member <- account <- transaction
