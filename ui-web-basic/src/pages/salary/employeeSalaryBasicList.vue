<template>
  <div>
    <head-tab active="employeeSalaryBasicList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.basicSalary')}}</el-button>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.seniorityWage')}}</el-button>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.reportFee')}}</el-button>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.socialSecurity')}}</el-button>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.withholding')}}</el-button>
        <el-button type="primary" @click="itemAdd" v-permit="'sys:dictEnum:edit'">{{$t('employeeSalaryBasicList.deposit')}}</el-button>
      </el-row>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('employeeSalaryBasicList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="id" :label="$t('employeeSalaryBasicList.id')" sortable width="150"></el-table-column>
        <el-table-column prop="type" :label="$t('employeeSalaryBasicList.type')"></el-table-column>
        <el-table-column prop="employeeName" :label="$t('employeeSalaryBasicList.employeeName')"></el-table-column>
        <el-table-column prop="shouldGet" :label="$t('employeeSalaryBasicList.shouldGet')"></el-table-column>
        <el-table-column prop="effectiveDate" :label="$t('employeeSalaryBasicList.effectiveDate')"></el-table-column>
        <el-table-column prop="processStatus" :label="$t('employeeSalaryBasicList.processStatus')"></el-table-column>
        <el-table-column fixed="right" :label="$t('employeeSalaryBasicList.operation')" width="140">
          <template scope="scope">
            <el-button size="small" @click.native="itemAction(scope.row.id,'edit')">{{$t('employeeSalaryBasicList.edit')}}</el-button>
            <el-button size="small" @click.native="itemAction(scope.row.id,'delete')">{{$t('employeeSalaryBasicList.delete')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  export default{
    data() {
      return {
        page:{},
        searchText:"",
        formData:{
          extra:{}
        },
        initPromise:{},
        formLabelWidth: '25%',
        formVisible: false,
        pageLoading: false
      };
    },
    methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },
      pageRequest() {
        this.pageLoading = true;
        this.setSearchText();
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/basic/salary/employeeSalaryBasic?'+qs.stringify(submitData)).then((response) => {
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
      },itemAdd(){
        this.$router.push({ name: 'employeeSalaryBasicForm'})
      },itemAction:function(id,action){
        if(action=="edit") {
          this.$router.push({ name: 'employeeSalaryBasicForm', query: { id: id }})
        } else if(action=="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
            axios.get('/api/basic/salary/employeeSalaryBasic/delete',{params:{id:id}}).then((response) =>{
              this.$message(response.data.message);
              this.pageRequest();
            });
          }).catch(()=>{});
        }
      }
    },created () {
      var that = this;
      that.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/basic/salary/employeeSalaryBasic/getQuery').then((response) =>{
        that.formData=response.data;
      });

    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

