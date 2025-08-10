<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="分类名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入分类名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable>
          <el-option :key="'0'" label="正常" :value="'0'" />
          <el-option :key="'1'" label="禁用" :value="'1'" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:category:add']"
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:category:remove']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:category:export']"
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="exportLoading"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:category:import']"
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
        >导入</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="分类名" align="center" prop="name" />
      <el-table-column label="描述" align="center" prop="description" />
      <el-table-column prop="status" label="状态" align="center">
        <template slot-scope="scope">
          <el-switch
            v-hasPermission="['content:category:edit']"
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
          <span v-if="!checkPermission(['content:category:edit'])">
            {{ scope.row.status === '0' ? '正常' : '禁用' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-hasPermission="['content:category:edit']"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            v-hasPermission="['content:category:remove']"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      :page-size.sync="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-sizes="[10, 20, 30, 40]"
      :current-page.sync="queryParams.page"
      @current-change="getList"
      @size-change="getList"
    />

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option :key="'0'" label="正常" :value="'0'" />
            <el-option :key="'1'" label="禁用" :value="'1'" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 分类导入对话框 -->
    <el-dialog :title="importTitle" :visible.sync="importOpen" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :disabled="importLoading"
        :on-change="handleFileChange"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :on-error="handleFileError"
        :auto-upload="false"
        :file-list="fileList"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            仅允许导入xls、xlsx格式文件。
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
          </div>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="importLoading" @click="submitFileForm">确 定</el-button>
        <el-button @click="importOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategory, getCategory, delCategory, addCategory, updateCategory, exportCategory, importCategory } from '@/api/content/category'

export default {
  name: 'Category',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 分类表格数据
      categoryList: null,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 导入参数
      importOpen: false,
      importLoading: false,
      importTitle: "分类导入",
      fileList: [],
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        name: null,
        description: null,
        metaKeywords: null,
        metaDescription: null,
        status: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    // 调试权限信息
    console.log('当前用户权限:', this.$store.getters.permissions)
    console.log('当前用户角色:', this.$store.getters.roles)
    console.log('是否有分类列表权限:', this.$store.getters.permissions.includes('content:category:list'))
    this.getList()
  },
  methods: {
    /** 检查权限 */
    checkPermission(permissions) {
      const userPermissions = this.$store.getters.permissions
      return permissions.some(permission => userPermissions.includes(permission))
    },
    /** 查询分类列表 */
    getList() {
      console.log('开始获取分类列表，查询参数:', this.queryParams)
      this.loading = true
      listCategory(this.queryParams).then(response => {
        console.log('分类列表API响应:', response)
        this.categoryList = response.rows
        this.total = response.total
        console.log('设置分类列表数据:', this.categoryList)
        console.log('设置总数:', this.total)
        this.loading = false
      }).catch(error => {
        console.error('获取分类列表失败:', error)
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        pid: null,
        description: null,
        metaKeywords: null,
        metaDescription: null,
        status: 0,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.page = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加分类'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCategory(id).then(response => {
        this.form = response
        this.open = true
        this.title = '修改分类'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCategory(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addCategory(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除分类编号为"' + ids + '"的数据项？').then(function() {
        return delCategory(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有分类数据？').then(() => {
        this.exportLoading = true
        return exportCategory()
      }).then(response => {
        // this.$download.name(response.msg)
        this.exportLoading = false
      }).catch(() => {})
    },
    /** 导入按钮操作 */
    handleImport() {
      this.importTitle = "分类导入"
      this.importOpen = true
      this.fileList = []
    },
    /** 下载模板操作 */
    importTemplate() {
      this.$modal.confirm('是否下载导入模板？').then(() => {
        return exportCategory()
      }).then(response => {
        this.$modal.msgSuccess("下载成功")
      }).catch(() => {})
    },
    // 文件选择变化处理
    handleFileChange(file, fileList) {
      this.fileList = fileList
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.importLoading = true
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.importLoading = false
      this.importOpen = false
      this.fileList = []
      this.$refs.upload.clearFiles()
      this.$modal.msgSuccess("导入成功")
      this.getList()
    },
    // 文件上传失败处理
    handleFileError(err, file, fileList) {
      this.importLoading = false
      this.$modal.msgError("导入失败")
    },
    // 提交上传文件
    submitFileForm() {
      if (this.fileList.length === 0) {
        this.$modal.msgError("请选择要上传的文件")
        return
      }
      
      this.importLoading = true
      const file = this.fileList[0].raw
      
      importCategory(file).then(response => {
        this.importLoading = false
        this.importOpen = false
        this.fileList = []
        this.$refs.upload.clearFiles()
        this.$modal.msgSuccess("导入成功")
        this.getList()
      }).catch(error => {
        this.importLoading = false
        this.$modal.msgError("导入失败：" + (error.msg || error.message || "未知错误"))
      })
    },
    /** 状态修改 */
    handleStatusChange(row) {
      const text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"分类吗？').then(function() {
        return updateCategory(row);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    }
  }
}
</script>
