<template>
  <div>
    <head-tab active="afterSaleList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" v-permit="'crm:afterSale:edit'">{{$t('afterSaleList.add')}}</el-button>
        <el-button type="primary" @click="itemEdit" icon="edit" v-permit="'crm:afterSale:edit'">{{$t('afterSaleList.edit')}}</el-button>
        <el-button type="primary" @click="itemSyn" icon="plus" v-permit="'crm:afterSale:edit'">{{$t('afterSaleList.syn')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" v-permit="'crm:afterSale:view'">过滤或导出</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('afterSaleList.filter')" v-model="formVisible" size="medium" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="4">
            <el-col :span="11">
              <el-form-item :label="$t('afterSaleList.areaDepot')" >
                <el-input v-model="formData.depotName" auto-complete="off" :placeholder="$t('afterSaleList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.createdDate')">
                <date-range-picker v-model="formData.createdRange"></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.toCompanyDate')">
                <date-range-picker v-model="formData.toCompanyDateRange" ></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.fromCompanyDate')" >
                <date-range-picker v-model="formData.fromCompanyDateRange"></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.toStoreDate')">
                <date-range-picker v-model="formData.toStoreDateRange" ></date-range-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('afterSaleList.bill')" >
                <el-input type="textarea" v-model="formData.businessIdStr" auto-complete="off" :placeholder="$t('afterSaleList.blankOrComma')"  :autosize="{ minRows: 2, maxRows: 5}"></el-input>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.toAreaProductIme')" >
                <el-input type="textarea" v-model="formData.toAreaImeStr" auto-complete="off" :placeholder="$t('afterSaleList.blankOrComma')"  :autosize="{ minRows: 2, maxRows: 5}"></el-input>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.badProductIme')" >
                <el-input type="textarea" v-model="formData.badImeStr" auto-complete="off" :placeholder="$t('afterSaleList.blankOrComma')"  :autosize="{ minRows: 2, maxRows: 5}"></el-input>
              </el-form-item>
              <el-form-item :label="$t('afterSaleList.createdBy')" >
               <!-- <el-input type="textarea" v-model="formData.createdBy" auto-complete="off" :placeholder="$t('afterSaleList.blankOrComma')"  :autosize="{ minRows: 2, maxRows: 5}"></el-input>-->
                <account-select v-model="formData.createdAccounts" :multiple="true"></account-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="exportData()">{{$t('afterSaleList.export')}}</el-button>
          <el-button type="primary" @click="search()">{{$t('afterSaleList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('afterSaleList.loading')" @sort-change="sortChange" stripe border>
      <!--  <el-table-column fixed prop="businessId" :label="$t('afterSaleList.bill')" sortable></el-table-column>-->
        <el-table-column prop="badProductIme" :label="$t('afterSaleList.badProductIme')"></el-table-column>
        <el-table-column prop="badProductName" :label="$t('afterSaleList.badProductName')" ></el-table-column>
        <el-table-column prop="toAreaProductIme" :label="$t('afterSaleList.toAreaProductIme')"></el-table-column>
        <el-table-column prop="toAreaProductName" :label="$t('afterSaleList.toAreaProductName')"></el-table-column><!--modify-->
        <el-table-column prop="areaDepotName" :label="$t('afterSaleList.areaDepot')"></el-table-column>
        <el-table-column prop="packageStatus" :label="$t('afterSaleList.package')" ></el-table-column>
        <el-table-column prop="memory" :label="$t('afterSaleList.memory')" ></el-table-column>
        <el-table-column prop="toStoreType":label="$t('afterSaleList.toStoreType')"></el-table-column>
        <el-table-column prop="toStoreRemarks" :label="$t('afterSaleList.toStoreDateRemarks')"></el-table-column>
        <el-table-column prop="toStoreDate" :label="$t('afterSaleList.toStoreDate')"></el-table-column>
        <el-table-column prop="toCompanyDate" :label="$t('afterSaleList.toCompanyDate')" ></el-table-column>
        <el-table-column prop="fromCompanyDate" :label="$t('afterSaleList.fromCompanyDate')"></el-table-column>
        <el-table-column prop="createdByName" :label="$t('afterSaleList.createdBy')"></el-table-column>
        <el-table-column prop="remarks" :label="$t('afterSaleList.remarks')"></el-table-column>
        <el-table-column prop="syn" :label="$t('afterSaleList.synFor')">
          <template scope="scope">
            <el-tag :type="scope.row.syn ? 'primary' : 'danger'">{{scope.row.syn | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('afterSaleList.operation')" width="140">
          <template scope="scope">
            <div class="action" ><el-button   size="small" @click.native="itemAction(scope.row.id, 'delete')" v-if="scope.row.deleted">删除</el-button></div>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  import accountSelect from "components/basic/account-select";
  export default {
    components:{
      accountSelect
    },
    data() {
      return {
        searchText:"",
        page:{},
        initPromise:{},
        formData:{extra:{}},
        formLabelWidth:"25%",
        formVisible: false,
        pageLoading: false,
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
        let submitData = util.deleteExtra(this.formData);
        axios.post('/api/ws/future/crm/afterSale',qs.stringify(submitData, {allowDots:true})).then((response) => {
            console.log(response.data);
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
        this.$router.push({ name: 'afterSaleForm'})
      },itemEdit(){
        this.$router.push({ name: 'afterSaleEditForm'})
      },itemSyn(){
        axios.get('/api/ws/future/crm/afterSale/synToFinance').then((response) =>{
          this.$message(response.data.message);
          this.pageRequest();
        })
      },exportData(){
        this.formVisible = false;
        var submitData = util.deleteExtra(this.formData);
        util.confirmBeforeExportData(this).then(() => {
          window.location.href='/api/ws/future/crm/afterSale/exportData?'+qs.stringify(submitData);
          this.pageRequest()
        }).catch(()=>{});
      },itemAction:function(id,action){
         if(action=="delete"){
           util.confirmBeforeDelRecord(this).then(() => {
             axios.get('/api/ws/future/crm/afterSale/delete',{params:{id:id}}).then((response) =>{
               this.$message(response.data.message);
               this.pageRequest();
             })
           }).catch(()=>{});
        }
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/ws/future/crm/afterSale/getQuery').then((response) =>{
        this.formData=response.data;
      });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

