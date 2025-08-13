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
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入文章标题"
          clearable
          size="small"
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文章摘要" prop="summary">
        <el-input
          v-model="queryParams.summary"
          placeholder="请输入文章摘要"
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
          type="success"
          plain
          icon="el-icon-refresh-right"
          size="mini"
          :disabled="multiple"
          @click="handleBatchRestore"
          v-hasPermission="['content:recyclebin:restore']"
        >批量恢复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleBatchDelete"
          v-hasPermission="['content:recyclebin:delete']"
        >永久删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-delete-solid"
          size="mini"
          @click="handleClearAll"
          v-hasPermission="['content:recyclebin:clear']"
        >清空回收站</el-button>
      </el-col>

    </el-row>

    <!-- 数据表格 -->
    <el-table 
      v-loading="loading"
      :data="articleList" 
      style="width: 100%" 
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="文章ID" align="center" width="80" />
      <el-table-column prop="title" label="标题" align="center" show-overflow-tooltip />
      <el-table-column prop="thumbnail" label="缩略图" align="center" width="100">
        <template slot-scope="scope">
          <el-avatar
            v-if="scope.row.thumbnail"
            :src="scope.row.thumbnail"
            size="small"
            shape="square"
          />
          <span v-else class="text-muted">无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="summary" label="摘要" align="center" show-overflow-tooltip />
      <el-table-column label="分类" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ getCategoryName(scope.row.categoryId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="标签" align="center" width="150">
        <template slot-scope="scope">
          <div v-if="getTagArray(scope.row.tagIds).length > 0" class="tags-container">
            <el-tag
              v-for="tagName in getTagArray(scope.row.tagIds)"
              :key="tagName"
              size="mini"
              type="info"
              class="tag-item"
            >
              {{ tagName }}
            </el-tag>
          </div>
          <span v-else class="text-muted">无标签</span>
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
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handlePreview(scope.row)"
            v-hasPermission="['content:recyclebin:query']"
          >预览</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh-right"
            style="color: #67C23A"
            @click="handleRestore(scope.row)"
            v-hasPermission="['content:recyclebin:restore']"
          >恢复</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            style="color: #F56C6C"
            @click="handleDelete(scope.row)"
            v-hasPermission="['content:recyclebin:delete']"
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

    <!-- 文章预览对话框 -->
    <el-dialog
      title="文章预览"
      :visible.sync="previewDialogVisible"
      width="70%"
      :close-on-click-modal="false"
    >
      <div v-if="currentArticle">
        <h2>{{ currentArticle.title }}</h2>
        <div v-if="currentArticle.thumbnail" style="margin-bottom: 15px;">
          <strong>缩略图：</strong><br>
          <img :src="currentArticle.thumbnail" alt="文章缩略图" style="max-width: 200px; max-height: 150px; border-radius: 4px; margin-top: 5px;">
        </div>
        <p><strong>摘要：</strong>{{ currentArticle.summary }}</p>
        <p><strong>分类：</strong>{{ getCategoryName(currentArticle.categoryId) }}</p>
        <p><strong>标签：</strong>
          <div v-if="getTagArray(currentArticle.tagIds).length > 0" class="tags-container" style="display: inline-block;">
            <el-tag
              v-for="tagName in getTagArray(currentArticle.tagIds)"
              :key="tagName"
              size="small"
              type="info"
              class="tag-item"
            >
              {{ tagName }}
            </el-tag>
          </div>
          <span v-else class="text-muted">无标签</span>
        </p>
        <p><strong>创建时间：</strong>{{ currentArticle.createTime }}</p>
        <div class="content-preview">
          <strong>内容预览：</strong>
          <div v-html="currentArticle.content || '无内容'" style="margin-top: 10px; border: 1px solid #eee; padding: 15px; border-radius: 4px; max-height: 400px; overflow-y: auto;"></div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="handleRestore(currentArticle)">恢复文章</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDeletedArticles,
  restoreArticle,
  permanentDeleteArticle,
  batchRestoreArticles,
  batchPermanentDeleteArticles,
  clearArticleRecycleBin
} from '@/api/content/recyclebin'
import { listAllTag } from '@/api/content/tag'
import { listAllCategory } from '@/api/content/category'

export default {
  name: 'ArticlesRecycle',
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
      // 文章表格数据
      articleList: [],
      // 所有标签列表
      allTags: [],
      // 所有分类列表
      allCategories: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示预览对话框
      previewDialogVisible: false,
      // 当前预览的文章
      currentArticle: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        summary: null
      }
    }
  },
  async created() {
    // 先获取标签和分类，再获取文章列表
    try {
      await Promise.all([
        this.getAllTags(),
        this.getAllCategories()
      ])
      this.getList()
    } catch (error) {
      console.error('数据加载失败，仍然获取文章列表:', error)
      this.getList()
    }
  },
  methods: {
    /** 查询文章列表 */
    getList() {
      this.loading = true
      const params = { ...this.queryParams }
      
      listDeletedArticles(params).then(response => {
        this.articleList = response.rows || response.data || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 获取所有标签 */
    getAllTags() {
      return listAllTag().then(response => {
        // 根据API响应结构处理数据
        this.allTags = response.data || response.rows || response || []
      }).catch(error => {
        console.error('获取标签列表失败:', error)
        this.allTags = []
      })
    },
    /** 获取所有分类 */
    getAllCategories() {
      return listAllCategory().then(response => {
        // 根据API响应结构处理数据
        this.allCategories = response.data || response.rows || response || []
      }).catch(error => {
        console.error('获取分类列表失败:', error)
        this.allCategories = []
      })
    },
    /** 根据分类ID获取分类名称 */
    getCategoryName(categoryId) {
      if (!categoryId) {
        return '未分类'
      }
      
      // 确保allCategories已加载
      if (!this.allCategories || this.allCategories.length === 0) {
        return '加载中...'
      }
      
      // 处理字符串和数字ID的匹配问题
      const category = this.allCategories.find(cat => 
        cat.id == categoryId || cat.id === categoryId || 
        String(cat.id) === String(categoryId)
      )
      
      return category ? category.name : '未分类'
    },
    /** 根据标签ID数组获取标签名称字符串 */
    getTagNames(tagIds) {
      if (!tagIds || !Array.isArray(tagIds) || tagIds.length === 0) {
        return ''
      }
      
      // 确保allTags已加载
      if (!this.allTags || this.allTags.length === 0) {
        return ''
      }
      
      const tagNames = tagIds
        .map(id => {
          // 处理字符串和数字ID的匹配问题
          const tag = this.allTags.find(tag => 
            tag.id == id || tag.id === id || 
            String(tag.id) === String(id)
          )
          return tag ? tag.name : null
        })
        .filter(name => name !== null)
        
      return tagNames.join(', ')
    },
    /** 根据标签ID数组获取标签名称数组 */
    getTagArray(tagIds) {
      if (!tagIds || !Array.isArray(tagIds) || tagIds.length === 0) {
        return []
      }
      
      // 确保allTags已加载
      if (!this.allTags || this.allTags.length === 0) {
        return []
      }
      
      return tagIds
        .map(id => {
          // 处理字符串和数字ID的匹配问题
          const tag = this.allTags.find(tag => 
            tag.id == id || tag.id === id || 
            String(tag.id) === String(id)
          )
          return tag ? tag.name : null
        })
        .filter(name => name !== null)
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
        title: null,
        summary: null
      }
      this.handleQuery()
    },
    /** 预览文章 */
    handlePreview(row) {
      this.currentArticle = row
      this.previewDialogVisible = true
    },
    /** 恢复文章 */
    handleRestore(row) {
      const id = row.id
      this.$modal.confirm('是否确认恢复标题为"' + row.title + '"的文章？').then(() => {
        return restoreArticle(id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('恢复成功')
        this.previewDialogVisible = false
      }).catch(() => {})
    },
    /** 永久删除文章 */
    handleDelete(row) {
      const id = row.id
      this.$modal.confirm('是否确认永久删除标题为"' + row.title + '"的文章？此操作不可恢复！').then(() => {
        return permanentDeleteArticle(id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('永久删除成功')
      }).catch(() => {})
    },
    /** 批量恢复 */
    handleBatchRestore() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请选择要恢复的文章')
        return
      }
      this.$modal.confirm('是否确认恢复选中的 ' + this.ids.length + ' 篇文章？').then(() => {
        return batchRestoreArticles(this.ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('批量恢复成功')
      }).catch(() => {})
    },
    /** 批量永久删除 */
    handleBatchDelete() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请选择要删除的文章')
        return
      }
      this.$modal.confirm('是否确认永久删除选中的 ' + this.ids.length + ' 篇文章？此操作不可恢复！').then(() => {
        return batchPermanentDeleteArticles(this.ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('批量删除成功')
      }).catch(() => {})
    },
    /** 清空回收站 */
    handleClearAll() {
      this.$modal.confirm('是否确认清空文章回收站？此操作将永久删除所有已删除的文章，不可恢复！').then(() => {
        return clearArticleRecycleBin()
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('清空成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.content-preview {
  margin-top: 15px;
}

.text-muted {
  color: #999;
  font-size: 12px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
  align-items: center;
}

.tag-item {
  margin: 2px;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-display {
  color: #409EFF;
  font-size: 12px;
  background-color: #ecf5ff;
  padding: 2px 6px;
  border-radius: 3px;
  border: 1px solid #d9ecff;
  display: inline-block;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
