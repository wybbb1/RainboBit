<template>
  <div>
    <!-- 搜索表单 -->
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="网站名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入网站名称"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="网站地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入网站地址"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
        >搜索</el-button>
        <el-button
          icon="el-icon-refresh"
          size="mini"
          @click="resetQuery"
        >重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:recyclebin:restore']"
          type="success"
          plain
          icon="el-icon-refresh-right"
          size="mini"
          :disabled="multiple"
          @click="handleBatchRestore"
        >批量恢复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:recyclebin:delete']"
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleBatchDelete"
        >永久删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermission="['content:recyclebin:clear']"
          type="warning"
          plain
          icon="el-icon-delete-solid"
          size="mini"
          @click="handleClearAll"
        >清空回收站</el-button>
      </el-col>

    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="linkList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="友链ID" align="center" width="80" />
      <el-table-column prop="name" label="网站名称" align="center" />
      <el-table-column prop="logo" label="网站图标" align="center" width="100">
        <template slot-scope="scope">
          <el-avatar
            v-if="scope.row.logo"
            :src="scope.row.logo"
            size="small"
            shape="square"
          />
          <span v-else class="text-muted">无图标</span>
        </template>
      </el-table-column>
      <el-table-column prop="address" label="网站地址" align="center" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link :href="scope.row.address" target="_blank" type="primary">
            {{ scope.row.address }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="网站描述" align="center" show-overflow-tooltip />
      <el-table-column prop="status" label="原审核状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="mini">审核通过</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="danger" size="mini">审核未通过</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="warning" size="mini">未审核</el-tag>
          <el-tag v-else type="info" size="mini">未知状态</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" width="160" />
      <el-table-column
        label="操作"
        align="center"
        width="180"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-hasPermission="['content:recyclebin:query']"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
          >预览</el-button>
          <el-button
            v-hasPermission="['content:recyclebin:restore']"
            size="mini"
            type="text"
            icon="el-icon-refresh-right"
            style="color: #67C23A"
            @click="handleRestore(scope.row)"
          >恢复</el-button>
          <el-button
            v-hasPermission="['content:recyclebin:delete']"
            size="mini"
            type="text"
            icon="el-icon-delete"
            style="color: #F56C6C"
            @click="handleDelete(scope.row)"
          >永久删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      :page-size.sync="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-sizes="[10, 20, 30, 40]"
      :current-page.sync="queryParams.pageNum"
      @current-change="getList"
      @size-change="getList"
    />

    <!-- 友链预览对话框 -->
    <el-dialog
      title="友链预览"
      :visible.sync="previewDialogVisible"
      width="50%"
      :close-on-click-modal="false"
    >
      <div v-if="currentLink" class="link-preview">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="logo-container">
              <el-avatar
                v-if="currentLink.logo"
                :src="currentLink.logo"
                :size="80"
                shape="square"
              />
              <div v-else class="no-logo">无图标</div>
            </div>
          </el-col>
          <el-col :span="18">
            <div class="link-info">
              <h3>{{ currentLink.name }}</h3>
              <p><strong>网站地址：</strong>
                <el-link :href="currentLink.address" target="_blank" type="primary">
                  {{ currentLink.address }}
                </el-link>
              </p>
              <p><strong>网站描述：</strong>{{ currentLink.description || '无描述' }}</p>
              <p><strong>审核状态：</strong>
                <el-tag v-if="currentLink.status === '0'" type="success" size="mini">审核通过</el-tag>
                <el-tag v-else-if="currentLink.status === '1'" type="danger" size="mini">审核未通过</el-tag>
                <el-tag v-else-if="currentLink.status === '2'" type="warning" size="mini">未审核</el-tag>
                <el-tag v-else type="info" size="mini">未知状态</el-tag>
              </p>
              <p><strong>创建时间：</strong>{{ currentLink.createTime }}</p>
              <p><strong>删除时间：</strong>{{ currentLink.deleteTime }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="handleRestore(currentLink)">恢复友链</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDeletedLinks,
  restoreLink,
  permanentDeleteLink,
  batchRestoreLinks,
  batchPermanentDeleteLinks,
  clearLinkRecycleBin
} from '@/api/content/recyclebin'

export default {
  name: 'LinksRecycle',
  data() {
    return {
      // 遮罩层
      loading: true,
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
      // 友链表格数据
      linkList: [],
      // 是否显示预览对话框
      previewDialogVisible: false,
      // 当前预览的友链
      currentLink: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        address: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询友链列表 */
    getList() {
      this.loading = true
      const params = { ...this.queryParams }

      listDeletedLinks(params).then(response => {
        this.linkList = response.rows || response.data || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        name: null,
        address: null
      }
      this.handleQuery()
    },
    /** 预览友链 */
    handlePreview(row) {
      this.currentLink = row
      this.previewDialogVisible = true
    },
    /** 恢复友链 */
    handleRestore(row) {
      const id = row.id
      this.$modal.confirm('是否确认恢复友链"' + row.name + '"？').then(() => {
        return restoreLink(id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('恢复成功')
        this.previewDialogVisible = false
      }).catch(() => {})
    },
    /** 永久删除友链 */
    handleDelete(row) {
      const id = row.id
      this.$modal.confirm('是否确认永久删除友链"' + row.name + '"？此操作不可恢复！').then(() => {
        return permanentDeleteLink(id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('永久删除成功')
      }).catch(() => {})
    },
    /** 批量恢复 */
    handleBatchRestore() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请选择要恢复的友链')
        return
      }
      this.$modal.confirm('是否确认恢复选中的 ' + this.ids.length + ' 个友链？').then(() => {
        return batchRestoreLinks(this.ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('批量恢复成功')
      }).catch(() => {})
    },
    /** 批量永久删除 */
    handleBatchDelete() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请选择要删除的友链')
        return
      }
      this.$modal.confirm('是否确认永久删除选中的 ' + this.ids.length + ' 个友链？此操作不可恢复！').then(() => {
        return batchPermanentDeleteLinks(this.ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('批量删除成功')
      }).catch(() => {})
    },
    /** 清空回收站 */
    handleClearAll() {
      this.$modal.confirm('是否确认清空友链回收站？此操作将永久删除所有已删除的友链，不可恢复！').then(() => {
        return clearLinkRecycleBin()
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('清空成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.link-preview {
  padding: 20px;
}

.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
}

.no-logo {
  width: 80px;
  height: 80px;
  border: 2px dashed #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}

.link-info h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
}

.link-info p {
  margin-bottom: 10px;
  line-height: 1.6;
}

.text-muted {
  color: #999;
  font-size: 12px;
}
</style>
