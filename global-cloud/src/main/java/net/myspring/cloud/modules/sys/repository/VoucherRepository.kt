package net.myspring.cloud.modules.sys.repository

import net.myspring.cloud.common.repository.BaseRepository
import net.myspring.cloud.modules.sys.domain.Voucher
import net.myspring.cloud.modules.sys.dto.VoucherDto
import net.myspring.cloud.modules.sys.web.query.VoucherQuery
import net.myspring.util.repository.MySQLDialect
import net.myspring.util.text.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/**
 * 凭证
 * Created by haos on 2017/5/24.
 */
interface VoucherRepository  : BaseRepository<Voucher, String>,VoucherRepositoryCustom{

}

interface VoucherRepositoryCustom{
    fun findPage(pageable: Pageable, voucherQuery: VoucherQuery): Page<VoucherDto>?
}

class VoucherRepositoryImpl @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): VoucherRepositoryCustom{
    override fun findPage(pageable: Pageable, voucherQuery: VoucherQuery): Page<VoucherDto>? {
        var sb = StringBuilder("select * from sys_gl_voucher where enabled=1 ");
        if(StringUtils.isNotBlank(voucherQuery.id)) {
            sb.append(" and id = :id ");
        }
        if(voucherQuery.fdate != null){
            sb.append(" and fdate = :fdate ")
        }
        if(StringUtils.isNotBlank(voucherQuery.status)){
            sb.append(" and status = :status ")
        }
        if(StringUtils.isNotBlank(voucherQuery.createdBy)){
            sb.append(" and created_by = :createdBy ")
        }
        if(StringUtils.isNotBlank(voucherQuery.companyName)){
            sb.append(" and company_name = :companyName ")
        }
        var pageableSql = MySQLDialect.getInstance().getPageableSql(sb.toString(),pageable);
        var countSql = MySQLDialect.getInstance().getCountSql(sb.toString());
        var list = namedParameterJdbcTemplate.query(pageableSql, BeanPropertySqlParameterSource(voucherQuery), BeanPropertyRowMapper(VoucherDto::class.java));
        var count = namedParameterJdbcTemplate.queryForObject(countSql, BeanPropertySqlParameterSource(voucherQuery),Long::class.java);
        return PageImpl(list,pageable,count);
    }
}