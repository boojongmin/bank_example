package boojongmin.bank.consumer

import boojongmin.bank.*
import boojongmin.bank.LogStep.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

class ConsumerSerivce(val cache: ConcurrentHashMap<Int, Member>, val es: ExecutorService, val consumer: KafkaConsumer<String, String>, val mapper: ObjectMapper) {

    fun aynscConsume() {
        consumer.subscribe(LogStep.values().map { it.name })

        Thread {
        while (true) {
            val records = consumer.poll(500)
            for (record in records) {
                // 최대 16명의 데이터 병렬 처리
                // ExecutorService MAX THREAD 16개.
                es.submit {
                    try {
                            consume(record)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }.start()
    }

    private fun consume(record: ConsumerRecord<String, String>) {
        val topic = record.topic()
        if (topic.startsWith(TOPIC_PFEFIX)) {
            processData(topic, record.value())

        } else {
            throw IllegalStateException("get message on topic " + record.topic())
        }
    }

    private fun processData(topic: String, json: String) {
        val step: LogStep = LogStep.valueOf(topic)
        when (step) {
            BANK_JOIN -> {
                val log: MemberLog = jsonDeserialize(json)
                addCache(log)
            }
            BANK_CREATE_ACCOUNT -> {
                val log: AccountLog = jsonDeserialize(json)
                addCache(log)
            }
            BANK_DEPOSIT -> {
                val log: DepositLog = jsonDeserialize(json)
                addCache(log)
            }
            BANK_WITHDRAW -> {
                val log: WithdrawLog = jsonDeserialize(json)
                addCache(log)
            }
            BANK_TRANSFER -> {
                val log: TransferLog = jsonDeserialize(json)
                addCache(log)
            }
            else -> println("---> unknown")
        }
    }

    private inline fun <reified T> jsonDeserialize(json: String): T {
        return mapper.readValue(json, T::class.java)
    }

    private fun addCache(log: Log) {
        when(log) {
            is MemberLog -> {
                cache[log.key] =  Member(log.key, log.name, log.createdAt)
            }
            else -> {
                val member: Member? = cache[log.key]
                if (member == null) {
                    print("can't find member in cache: ${log.key}")
                    return
                }
                when(log) {
                    is AccountLog -> {
//                        println(log::class.java)
                        member.accounts.add(Account(member, log.accountNumber))
                    }
                    is DepositLog -> {
                        try {
                            val account = member.accounts.first()
                            account.transactions.add(DepositTransaction(account, log.amount))
                        }catch (e: java.lang.Exception) {
                            println(">>> " + member.name)
//                            e.printStackTrace()
                        }
                    }
                    is WithdrawLog -> {
                        var account = member.accounts.first()
                        account.transactions.add(DepositTransaction(account, log.amount))
                    }
                    is TransferLog -> {
                        var account = member.accounts.first()
                        account.transactions.add(DepositTransaction(account, log.amount))
                    }
                    else -> {
                        println("something wrong: ${log}")
                    }
                }
            }
        }

    }
}