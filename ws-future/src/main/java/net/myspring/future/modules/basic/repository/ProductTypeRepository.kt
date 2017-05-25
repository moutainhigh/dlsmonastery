package net.myspring.future.modules.basic.repository

import net.myspring.future.common.repository.BaseRepository
import net.myspring.future.modules.basic.domain.ProductType
import net.myspring.future.modules.basic.dto.ProductTypeDto
import net.myspring.future.modules.basic.web.query.ProductTypeQuery
import org.apache.ibatis.annotations.Param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import javax.persistence.EntityManager

/**
 * Created by zhangyf on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("productTypes"))
interface ProductTypeRepository : BaseRepository<ProductType,String>,ProductTypeRepositoryCustom {

    @Cacheable
    override fun findOne(id: String): ProductType

    override fun findAll(): MutableList<ProductType>

    @CachePut(key = "#id")
    fun save(productType: ProductType): Int

    @Query("""
        SELECT t1.*
        FROM crm_product t1
        where t1.enabled=1
    """, nativeQuery = true)
    fun findAllEnabled(): MutableList<ProductType>

    @Query("""
        SELECT t1.*
        FROM crm_product t1
        where t1.enabled=1
        and t1.id in ?1
    """, nativeQuery = true)
    fun findByIds(ids: MutableList<String>): MutableList<ProductType>

//    fun findList(@Param("p") map: Map<String, Any>): MutableList<ProductType>

    @Query("""
        SELECT t1.*
        FROM crm_product t1
        where t1.enabled=1
        and t1.demo_phone_type_id in ?1
    """, nativeQuery = true)
    fun findByDemoPhoneTypeIds(dempProductTypeIds: MutableList<String>): MutableList<ProductType>

    fun findByNameLike(@Param("name") name: String): MutableList<ProductType>

//    fun updateDemoPhoneType(@Param("demoPhoneTypeId") demoPhoneTypeId: String, @Param("list") ids: MutableList<String>): Int
//
//    fun updateDemoPhoneTypeToNull(demoPhoneTypeId: String): Int

    @Query("""
        SELECT t1.* from crm_product_type t1
        where t1.score_type=1
        and t1.enabled = 1
        and t1.price1 is not null order by t1.name
    """, nativeQuery = true)
    fun findAllScoreType(): MutableList<ProductType>

    fun findByDemoPhoneTypeId(demoPhoneTypeId: String): MutableList<ProductType>
}

interface ProductTypeRepositoryCustom{
    fun findPage(pageable: Pageable, productTypeQuery: ProductTypeQuery): Page<ProductTypeDto>
}

class ProductTypeRepositoryImpl @Autowired constructor(val entityManager: EntityManager):ProductTypeRepositoryCustom{

    override fun findPage(pageable: Pageable, productTypeQuery: ProductTypeQuery): Page<ProductTypeDto> {
        val sb = StringBuffer()

        var query = entityManager.createNativeQuery(sb.toString(), ProductTypeDto::class.java)

        return query.resultList as Page<ProductTypeDto>
    }
}