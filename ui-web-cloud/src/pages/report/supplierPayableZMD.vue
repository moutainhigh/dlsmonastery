<template>
  <div>
    <head-tab active="supplierPayableZMD"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="formVisible = true">过滤&导出</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" title="过滤" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="7">
            <el-col :span="24">
              <el-form-item label="开始日期" >
                <date-picker placeholder="选择开始日期" v-model="formData.dateStart"></date-picker>
              </el-form-item>
              <el-form-item label="截止日期" >
                <date-picker placeholder="选择截止日期" v-model="formData.dateEnd"></date-picker>
              </el-form-item>
              <el-form-item label="部门名称" >
                <el-select v-model="formData.departmentIdList"  multiple filterable remote placeholder="请输入关键词" :remote-method="remoteDepartment" :loading="remoteLoading">
                  <el-option v-for="item in departments" :key="item.fdeptId" :label="item.ffullName" :value="item.fdeptId"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()" icon="search">搜索</el-button>
          <el-button type="primary" @click="exportData()" icon="upload">导出</el-button>
        </div>
      </search-dialog>
      <el-dialog v-model="detailVisible" size="large">
        <el-table :data="detail" :row-class-name="tableRowClassName" v-loading="detailLoading" element-loading-text="拼命加载中....." border>
          <el-table-column prop="billType" label="业务类型"></el-table-column>
          <el-table-column prop="billNo" label="单据编号"></el-table-column>
          <el-table-column prop="date" label="单据日期"></el-table-column>
          <el-table-column prop="materialName" label="商品名称"></el-table-column>
          <el-table-column prop="qty" label="数量"></el-table-column>
          <el-table-column prop="price" label="单价" :formatter="moneyFormatter"></el-table-column>
          <el-table-column prop="amount" label="金额" :formatter="moneyFormatter"></el-table-column>
          <el-table-column prop="payableAmount" label="应付" :formatter="moneyFormatter"></el-table-column>
          <el-table-column prop="actualPayAmount" label="实付" :formatter="moneyFormatter"></el-table-column>
          <el-table-column prop="endAmount" label="期末" :formatter="moneyFormatter"></el-table-column>
          <el-table-column prop="note" label="摘要"></el-table-column>
        </el-table>
      </el-dialog>
      <el-table :data="summary" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" element-loading-text="拼命加载中....." stripe border>
        <el-table-column fixed prop="supplierName" label="供应商名称" sortable width="200"></el-table-column>
        <el-table-column prop="departmentName" label="部门名称"></el-table-column>
        <el-table-column prop="beginAmount" label="期初应付" :formatter="moneyFormatter"></el-table-column>
        <el-table-column prop="payableAmount" label="应付金额" :formatter="moneyFormatter"></el-table-column>
        <el-table-column prop="actualPayAmount" label="实付金额" :formatter="moneyFormatter"></el-table-column>
        <el-table-column prop="endAmount" label="期末应付" :formatter="moneyFormatter"></el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template scope="scope">
            <el-button size="small" @click="detailAction(scope.row.supplierId,scope.row.departmentId)">详细</el-button>
            <el-button size="small" @click="exportDetailOne(scope.row.supplierId,scope.row.departmentId)">导出</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<style>
  .el-table .info-row {
    background: #c9e5f5;
  }

  .el-table .danger-row {
    background: #f2dede;
  }

  .el-table .warning-row {
    background: #FFEE99;
  }
  .el-table .default-row{
    background:#FFFFFF;
  }
</style>
<script>
  export default {
    data() {
      return {
        page:{},
        summary:[],
        departments:[],
        detail:[],
        formData: {
          extra:{}
        },
        searchText:"",
        initPromise:{},
        formLabelWidth: '28%',
        remoteLoading:false,
        formVisible: false,
        detailVisible:false,
        pageLoading: false,
        detailLoading:false,
        pageHeight:'',
      };
    },
    methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },
      moneyFormatter(row,col){
        return util.moneyFormatter(row,col)
      },
      pageRequest() {
        this.pageLoading = true;
        this.setSearchText();
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/global/cloud/kingdee/bdDepartment?'+ qs.stringify(submitData)).then((response) => {
          let departmentIdList = new Array();
          let departments = response.data.content;
          for (let item in departments) {
            departmentIdList.push(departments[item].fdeptId);
          }
          submitData.departmentIdList = departmentIdList;
          if (submitData.departmentIdList.length !== 0) {
            axios.get('/api/global/cloud/report/supplierPayableZMD/list?' + qs.stringify(submitData)).then((response) => {
              this.summary = response.data;
              submitData.departmentIdList = [];
            });
          }
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.sort=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },exportData(){
        util.confirmBeforeExportData(this).then(() => {
          window.location.href='/api/global/cloud/report/supplierPayableZMD/export?'+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      },exportDetailOne(supplierId,departmentId){
        if(supplierId !== null) {
          let submitDetail = Object();
          submitDetail.supplierIdList = supplierId;
          submitDetail.departmentIdList = departmentId;
          submitDetail.dateStart = this.formData.dateStart;
          submitDetail.dateEnd = this.formData.dateEnd;
          window.location.href = '/api/global/cloud/report/supplierPayable/exportDetailOne?'+qs.stringify(submitDetail);
        }
      },detailAction:function(supplierId,departmentId){
        this.detailLoading = true;
        if(supplierId !== null) {
          let submitDetail = Object();
          submitDetail.supplierIdList = supplierId;
          submitDetail.departmentIdList = departmentId;
          submitDetail.dateStart = this.formData.dateStart;
          submitDetail.dateEnd = this.formData.dateEnd;
          axios.get('/api/global/cloud/report/supplierPayableZMD/detail',{params:submitDetail}).then((response) =>{
            this.detail = response.data;
            this.detailLoading = false;
            this.detailVisible = true;
          })
        }
      },tableRowClassName(row, index) {
        if (row.index === 0) {
          return "default-row";
        } else if (row.documentStatus !== "C" && row.billType !== "期初应付" && row.billType !== "期末应付") {
          return "danger-row ";
        } else if (row.billType === "期初应付" || row.billType === "期末应付") {
          return "warning-row";
        } else if (row.index % 2 === 0) {
          return "default-row";
        } else if (row.index % 2 !== 0) {
          return "info-row";
        }
      },remoteDepartment(query) {
        if (query !== '') {
          this.remoteLoading = true;
          axios.get('/api/global/cloud/kingdee/bdDepartment/findByNameLike',{params:{name:query}}).then((response)=>{
            this.departments = response.data;
            this.remoteLoading = false;
          })
        } else {
          this.departments = {};
        }
      },
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    },created () {
      let that = this;
      that.pageHeight = 0.74*window.innerHeight;
      that.initPromise = axios.get('/api/global/cloud/kingdee/bdDepartment/getQueryForSupplierPayable').then((response) =>{
        that.formData = response.data;
        util.copyValue(that.$route.query,that.formData);
        that.pageRequest();
      });
    }
  };
</script>

