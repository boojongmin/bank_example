package boojongmin.bank.consumer

import boojongmin.bank.*
import boojongmin.bank.Factory.createConsumer
import boojongmin.bank.Factory.createObjectMapper
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.apache.kafka.clients.consumer.Consumer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool

class ConsumerRunnerFactory {
    fun process(consumer: Consumer<String, String>): Triple<Int, ConsumerRunner, ConcurrentHashMap<Int, Member>> {
        val cache = ConcurrentHashMap<Int, Member>()
        val partitions = consumer.partitionsFor(BANK_JOIN.name)
        val partitionsCount = partitions?.size ?: 1
        val MAX_THREAD_COUNT = if( partitionsCount >= 16 ) 16 else partitionsCount
        val mapper = createObjectMapper()
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        val service = ConsumerService(cache, mapper)
        val runner = createConsumerRunner(MAX_THREAD_COUNT, service)
        return Triple(partitionsCount, runner, cache)
    }

    fun createConsumerRunner(maxThreadCount: Int, service: IConsumerService): ConsumerRunner  {
        val es: ExecutorService = newFixedThreadPool(maxThreadCount)
        return ConsumerRunner(es, service, maxThreadCount)
    }
}

class ConsumerRunner(val es: ExecutorService, val service: IConsumerService, val MAX_THREAD_COUNT: Int) {
    fun run() {
        for(i in 1..MAX_THREAD_COUNT) {
            val consumer = createConsumer()
            es.submit(ConsumerRunnable(consumer, service, false))
        }
    }
}

class ConsumerRunnable(val consumer: Consumer<String, String>, val service: IConsumerService, val isTest: Boolean ): Runnable {
    override fun run() {
        try {
            while (true) {
                val records = consumer.poll(1000)
                for (record in records) {
                    service.consume(record.topic(), record.value())
                }
                try {
                    consumer.commitAsync()
                } catch (e: Exception) {
                    println("commit failed ${e.message}")
                }

                if(isTest) break
            }
        } catch (e: Exception){
            consumer.close()
        }
    }
}

interface IConsumerService {
    fun consume(topic: String, json: String)
}

class ConsumerService(val cache: ConcurrentHashMap<Int, Member>, val mapper: ObjectMapper): IConsumerService {
    override fun consume(topic: String, json: String) {
        try {
            process(topic, json)
        } catch (e: Exception) {
            println("processData failed ${e.message}")
        }
    }

    private fun process(topic: String, json: String) {
        val step: LogStep = LogStep.valueOf(topic)
        when (step) {
            BANK_JOIN -> {
                val log: JoinLog = mapper.deserialize(json)
                addToCache(log)
            }
            BANK_CREATE_ACCOUNT -> {
                val log: CreateAccountLog = mapper.deserialize(json)
                addToCache(log)
            }
            BANK_DEPOSIT -> {
                val log: DepositLog = mapper.deserialize(json)
                addToCache(log)
            }
            BANK_WITHDRAW -> {
                val log: WithdrawLog = mapper.deserialize(json)
                addToCache(log)
            }
            BANK_TRANSFER -> {
                val log: TransferLog = mapper.deserialize(json)
                addToCache(log)
            }
            else -> println("Invalid Topic.")
        }
    }


    private fun addToCache(log: Log) {
        when (log) {
            is JoinLog -> {
                cache[log.key] = Member(log.key, log.name, log.createdAt)
            }
            else -> {
                val member: Member? = cache[log.key]
                if (member == null) {
                    print("can't find member in cache: ${log.key}")
                    return
                }
                when (log) {
                    is CreateAccountLog -> {
                        member.accounts.add(Account(member, log.accountNumber))
                    }
                    is DepositLog -> {
                        try {
                            val account = member.accounts.first()
                            account.transactions.add(DepositTransaction(account, log.amount))
                        } catch (e: java.lang.Exception) {
                            println(">>> " + member.name)
                        }
                    }
                    is WithdrawLog -> {
                        var account = member.accounts.first()
                        account.transactions.add(WithdrawTransaction(account, log.amount))
                    }
                    is TransferLog -> {
                        var account = member.accounts.first()
                        account.transactions.add(TransferTransaction(account, log.amount, log.bank, log.outAccountNumber, log.name))
                    }
                    else -> {
                        println("Invalid Log: ${log}")
                    }
                }
            }
        }

    }
}