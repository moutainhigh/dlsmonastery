<template>
  <div>
    <head-tab active="permissionList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" v-permit="'sys:permission:edit'">{{$t('permissionList.add')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" v-permit="'sys:permission:view'">{{$t('permissionList.filter')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('permissionList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData"  :label-width="formLabelWidth">
              <el-form-item :label="$t('permissionList.name')">
                <el-input v-model="formData.name" auto-complete="off" :placeholder="$t('permissionList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('permissionList.permission')">
                <el-input v-model="formData.permission" auto-complete="off" :placeholder="$t('permissionList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('permissionList.menuName')">
                <el-input v-model="formData.menuName" auto-complete="off" :placeholder="$t('permissionList.likeSearch')"></el-input>
              </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('permissionList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('permissionList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column prop="name" :label="$t('permissionList.name')" sortable></el-table-column>
        <el-table-column prop="permission" :label="$t('permissionList.permission')" sortable></el-table-column>
        <el-table-column prop="menuName" :label="$t('permissionList.menuName')"></el-table-column>
        <el-table-column prop="locked" :label="$t('permissionList.locked')" sortable>
          <template scope="scope">
            <el-tag :type="scope.row.locked ? 'primary' : 'danger'">{{scope.row.locked | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" :label="$t('permissionList.remarks')"></el-table-column>
        <el-table-column fixed="right" :label="$t('permissionList.operation')" width="150">
          <template scope="scope">
              <el-button size="small" @click.native="itemAction(scope.row.id,'edit')" v-permit="'sys:permission:edit'">修改</el-button>
              <el-button size="small" type="danger" @click.native="itemAction(scope.row.id,'delete')" v-permit="'sys:permission:delete'">删除</el-button>
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
        page:{},
        searchText:"",
        formData:{
            extra:{}
        },
        initPromise:{},
        formLabelWidth: '25%',
        formVisible: false,
        pageLoading: false,
        remoteLoading:false
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
        axios.get('/api/basic/sys/permission?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.order=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },itemAdd(){
        this.$router.push({ name: 'permissionForm'})
      },itemAction:function(id,action){
        if(action=="edit") {
          this.$router.push({ name: 'permissionForm', query: { id: id }})
        } else if(action=="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
          axios.get('/api/basic/sys/permission/delete',{params:{id:id}}).then((response) =>{
            this.$message(response.data.message);
            this.pageRequest();
          });
        }).catch(()=>{});
        }
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/basic/sys/permission/getQuery').then((response) =>{
        this.formData=response.data;
    });
    },activated(){
        this.initPromise.then(()=>{
          this.pageRequest();
        })
    }
  };
</script>

