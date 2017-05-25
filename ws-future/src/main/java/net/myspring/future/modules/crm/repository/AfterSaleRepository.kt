package net.myspring.future.modules.crm.repository

import net.myspring.future.common.repository.BaseRepository
import net.myspring.future.modules.basic.domain.Bank
import net.myspring.future.modules.crm.domain.AfterSale
import net.myspring.future.modules.crm.domain.BankIn
import net.myspring.future.modules.crm.dto.AfterSaleDto
import net.myspring.future.modules.crm.dto.BankInDto
import net.myspring.future.modules.crm.dto.DemoPhoneDto
import net.myspring.future.modules.crm.web.query.AfterSaleQuery
import net.myspring.future.modules.crm.web.query.BankInQuery
import net.myspring.future.modules.crm.web.query.DemoPhoneQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EntityManager


interface AfterSaleRepository : BaseRepository<AfterSale, String>,AfterSaleRepositoryCustom {

    @Query("""
    SELECT t1.*
    FROM crm_after_sale t1,crm_product_ime t2
    WHERE t1.bad_product_ime_id=t2.id
        AND t2.ime in ?1
        """, nativeQuery = true)
    fun findByBadProductImeIn(imeList: MutableList<String>): MutableList<AfterSale>

    @Query("""
    SELECT MAX(t1.business_id)
    FROM crm_after_sale t1
    WHERE t1.created_date >= ?1
        """, nativeQuery = true)
    fun findMaxBusinessId(dateStart: LocalDate): String

}


interface AfterSaleRepositoryCustom{
    fun findPage(pageable: Pageable, afterSaleQuery : AfterSaleQuery): Page<AfterSale>?
}

class AfterSaleRepositoryImpl @Autowired constructor(val entityManager: EntityManager): AfterSaleRepositoryCustom {
    override fun findPage(pageable: Pageable, afterSaleQuery: AfterSaleQuery): Page<AfterSale>? {
        return null


    }


}