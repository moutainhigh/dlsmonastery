<template>
  <div>
    <head-tab active="officeChangeList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus"  v-permit="'hr:officeChange:edit'">{{$t('officeChangeList.add')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" >{{$t('officeChangeList.filter')}}</el-button>
        <el-button type="primary" @click="batchPass" icon="check">{{$t('officeChangeList.batchPass')}}</el-button>
        <el-button type="primary" @click="batchNoPass" icon="close">{{$t('officeChangeList.batchBack')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('officeChangeList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData"  :label-width="formLabelWidth">
          <el-form-item :label="$t('officeChangeList.createdDate')">
            <date-range-picker v-model="formData.createdDate"></date-range-picker>
          </el-form-item>
          <el-form-item :label="$t('officeChangeList.officeName')">
            <office-select v-model="formData.officeId"></office-select>
          </el-form-item>
          <el-form-item :label="$t('officeChangeList.type')">
            <el-select v-model="formData.type" clearable filterable :placeholder="$t('officeChangeList.selectGroup')">
              <el-option v-for="type in formData.extra.typeList" :key="type" :label="type" :value="type"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('officeChangeList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" @selection-change="selectionChange"  :element-loading-text="$t('officeChangeList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column type="selection" width="55" :selectable="checkSelectable"></el-table-column>
        <el-table-column fixed prop="id" :label="$t('officeChangeList.id')" sortable></el-table-column>
        <el-table-column  prop="type" :label="$t('officeChangeList.type')" sortable></el-table-column>
        <el-table-column  prop="officeName" :label="$t('officeChangeList.officeName')" sortable></el-table-column>
        <el-table-column  prop="oldLabel" :label="$t('officeChangeList.oldLabel')" sortable></el-table-column>
        <el-table-column  prop="newLabel" :label="$t('officeChangeList.newLabel')" sortable></el-table-column>
        <el-table-column  prop="processStatus" :label="$t('officeChangeList.processStatus')" sortable ></el-table-column>
        <el-table-column :label="$t('officeChangeList.operation')" width="140">
          <template scope="scope">
            <el-button size="small" @click.native="itemAction(scope.row.id,'detail')" v-permit="'hr:officeChange:view'" >{{$t('officeChangeList.detail')}}</el-button>
            <el-button size="small" @click.native="itemAction(scope.row.id,'delete')" v-permit="'hr:officeChange:delete'" v-if="scope.row.processStatus !== '已通过' &&scope.row.processStatus !== '未通过'">{{$t('officeChangeList.delete')}}</el-button>
            <el-button size="small" @click.native="itemAction(scope.row.id,'audit')" v-permit="'hr:officeChange:audit'" v-if="scope.row.processStatus !== '已通过' &&scope.row.processStatus !== '未通过'">{{$t('officeChangeList.audit')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  import officeSelect from "components/basic/office-select"
  export default {
    components:{
      officeSelect
    },
    data() {
      return {
        page:{},
        formData:{
          extra:{},
        },
        initPromise:{},
        searchText:"",
        selects:[],
        formLabelWidth: '25%',
        formVisible: false,
        pageLoading: false,
        show:false
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
        axios.get('/api/basic/hr/officeChange?'+qs.stringify(submitData)).then((response) => {
          console.log(response.data)
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
        this.show = false;
        this.pageRequest();
      },itemAdd(){
        this.$router.push({ name: 'officeChangeForm'})
      },itemAction:function(id,action){
        if(action=="detail") {
          this.$router.push({ name: 'officeChangeForm', query: { id: id,action:action }})
        } else if(action=="audit"){
          this.$router.push({ name: 'officeChangeForm', query: { id: id,action:action }})
        }else if(action="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
            axios.get('/api/basic/hr/officeChange/delete',{params:{id:id}}).then((response) =>{
              this.$message(response.data.message);
              this.pageRequest();
            })
          }).catch(()=>{});
        }
      },selectionChange(selection){
        this.selects = [];
        for (let each of selection) {
          this.selects.push(each.id);
        }
      },batchPass(){
        if(!this.selects || this.selects.length < 1){
          this.$message("请选择审批记录");
          return ;
        }
        util.confirmBeforeBatchPass(this).then(() => {
          this.submitDisabled = true;
          this.pageLoading = true;
          axios.get('/api/basic/hr/officeChange/batchPass',{params:{ids:this.selects, pass:'1'}}).then((response) =>{
            this.$message(response.data.message);
            this.pageLoading = false;
            this.submitDisabled = false;
            this.pageRequest();
          });
        }).catch(()=>{});
      },batchNoPass(){
        if(!this.selects || this.selects.length < 1){
          this.$message("请选择审批记录");
          return ;
        }
        util.confirmBeforeBatchPass(this).then(() => {
          this.submitDisabled = true;
          this.pageLoading = true;
          axios.get('/api/basic/hr/officeChange/batchPass',{params:{ids:this.selects, pass:'0'}}).then((response) =>{
            this.$message(response.data.message);
            this.pageLoading = false;
            this.submitDisabled = false;
            this.pageRequest();
          });
        }).catch(()=>{});
      },checkSelectable(row) {
        return row.processStatus !== '已通过' && row.processStatus !== '未通过'
      }
    },created () {
      var that=this;
      this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/basic/hr/officeChange/getQuery').then((response) =>{
        that.formData=response.data;
        console.log(this.formData.extra.typeList);
      });
    },activated() {
      this.initPromise.then(()=> {
        this.pageRequest();
      })
    }
  };
</script>

