<template>
  <div>
    <head-tab active="demoPhoneTypeList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" v-permit="'crm:demoPhoneType:edit'">{{$t('demoPhoneTypeList.add')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" v-permit="'crm:demoPhoneType:view'">{{$t('demoPhoneTypeList.filter')}}</el-button>
        <span  v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('demoPhoneTypeList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog" >
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item :label="$t('demoPhoneTypeList.name')" >
                <el-input v-model="formData.name" auto-complete="off" :placeholder="$t('demoPhoneTypeList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('demoPhoneTypeList.productTypeName')" >
                <el-input v-model="formData.productTypeName" auto-complete="off" :placeholder="$t('demoPhoneTypeList.likeSearch')"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('demoPhoneTypeList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('demoPhoneTypeList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column prop="name" :label="$t('demoPhoneTypeList.name')" sortable ></el-table-column>
        <el-table-column prop="limitQty" :label="$t('demoPhoneTypeList.limitQty')" sortable></el-table-column>
        <el-table-column prop="productTypeNames" :label="$t('demoPhoneTypeList.productTypeNames')"></el-table-column>
        <el-table-column prop="applyEndDate" :label="$t('demoPhoneTypeList.applyEndDate')" sortable></el-table-column>
        <el-table-column prop="renewEndDate" :label="$t('demoPhoneTypeList.renewEndDate')" sortable></el-table-column>
        <el-table-column column-key="createdBy" prop="createdByName" :label="$t('demoPhoneTypeList.createdBy')" sortable></el-table-column>
        <el-table-column prop="createdDate" :label="$t('demoPhoneTypeList.createdDate')" sortable></el-table-column>
        <el-table-column prop="remarks" :label="$t('demoPhoneTypeList.remarks')"></el-table-column>
        <el-table-column prop="locked" :label="$t('demoPhoneTypeList.locked')" width="120">
          <template scope="scope">
            <el-tag :type="scope.row.locked ? 'primary' : 'danger'">{{scope.row.locked | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" :label="$t('demoPhoneTypeList.enabled')" width="120">
          <template scope="scope">
            <el-tag :type="scope.row.enabled ? 'primary' : 'danger'">{{scope.row.enabled | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('demoPhoneTypeList.operation')" width="140">
          <template scope="scope">
            <el-button size="small" v-permit="'crm:demoPhoneType:edit'" @click.native="itemAction(scope.row.id,'edit')">{{$t('demoPhoneTypeList.edit')}}</el-button>
            <el-button size="small" v-permit="'crm:demoPhoneType:delete'" @click.native="itemAction(scope.row.id,'delete')">{{$t('demoPhoneTypeList.delete')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        pageLoading: false,
        pageHeight:600,
        page:{},
        searchText:"",
        formData:{
            extra:{}
        },
        initPromise:{},
        formLabelWidth: '28%',
        formVisible: false,
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
        axios.get('/api/ws/future/crm/demoPhoneType',{params:submitData}).then((response) => {
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
        this.$router.push({ name: 'demoPhoneTypeForm'})
      },itemAction:function(id,action){
        if(action=="edit") {
          this.$router.push({ name: 'demoPhoneTypeForm', query: { id: id }})
        } else if(action=="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
            axios.get('/api/ws/future/crm/demoPhoneType/delete', {params: {id: id}}).then((response) => {
              this.$message(response.data.message);
              this.pageRequest();
            })
          })
        }
      }
    },created () {
      var that = this;
      that.pageHeight = 0.74*window.innerHeight;
      this.initPromise=axios.get('/api/ws/future/crm/demoPhoneType/getQuery').then((response) => {
        this.formData = response.data;
      })
    },activated(){
    this.initPromise.then(()=>{
      this.pageRequest();
    });
  }
  };
</script>


