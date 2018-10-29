package boojongmin.bank

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class MemberTest {

    @Test
    fun createAccount() {
        var (number, name, _, accounts) = Member(1, "name_1")
        assertThat(number).isEqualTo(1)
        assertThat(name).isEqualTo("name_1")
        assertThat(accounts.size).isEqualTo(0)
    }
}