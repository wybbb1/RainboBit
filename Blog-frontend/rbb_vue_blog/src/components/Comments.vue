<template>
  <div class="comments-section">
    <h3 class="comments-title">
      <span class="comment-icon">💬</span>
      评论 ({{ allCommentsCount }})
    </h3>

    <div class="comment-list" v-if="comments.length > 0">
      <div 
        v-for="comment in comments" 
        :key="comment.id" 
        class="comment-wrapper"
      >
        <div class="comment-item root-comment">
          <div class="comment-body">
            <div class="comment-header">
              <div class="comment-author">
                <div class="user-avatar">
                  <img 
                    v-if="comment.avatar" 
                    :src="comment.avatar" 
                    :alt="comment.username || `用户${comment.createBy}`"
                    class="avatar-img"
                  >
                  <span v-else class="avatar-placeholder">
                    {{ (comment.username || `用户${comment.createBy}`).charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div class="author-info">
                  <span class="username">{{ comment.username || `用户${comment.createBy}` }}</span>
                  <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
                </div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            
            <div class="comment-actions" v-if="replyingTo !== comment.id">
              <button 
                class="reply-btn" 
                @click="toggleReply(comment.id)"
                :class="{ active: replyingTo === comment.id }"
              >
                <span class="reply-icon">↩️</span>
                回复
              </button>
              <button 
                v-if="comment.children && comment.children.length > 0"
                class="expand-btn" 
                @click="toggleExpand(comment.id)"
                :class="{ expanded: isExpanded(comment.id) }"
              >
                <span class="expand-icon">{{ isExpanded(comment.id) ? '▼' : '▶' }}</span>
                {{ isExpanded(comment.id) ? '收起' : `展开 (${comment.children.length})` }}
              </button>
            </div>
            
            <div v-if="replyingTo === comment.id" class="reply-form">
              <div class="textarea-wrapper">
                <textarea 
                  v-model="replyContent"
                  placeholder="写下你的回复..."
                  rows="3"
                  maxlength="300"
                ></textarea>
                <div class="char-count" :class="{ 'limit-warning': replyContent.length > 270 }">
                  {{ replyContent.length }}/300
                </div>
              </div>
              <div class="reply-actions">
                <button 
                  class="btn btn-primary" 
                  @click="submitReply(comment)" 
                  :disabled="submitting || !replyContent.trim() || replyContent.length < 2"
                >
                  {{ submitting ? '提交中...' : '提交回复' }}
                </button>
                <button class="btn btn-ghost" @click="cancelReply">取消</button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="comment.children && comment.children.length > 0 && isExpanded(comment.id)" class="children-comments">
          <div 
            v-for="childComment in comment.children" 
            :key="childComment.id"
            class="comment-item child-comment"
          >
            <div class="comment-body">
              <div class="comment-header">
                <div class="comment-author">
                  <div class="user-avatar child-avatar">
                    <img 
                      v-if="childComment.avatar" 
                      :src="childComment.avatar" 
                      :alt="childComment.username || `用户${childComment.createBy}`"
                      class="avatar-img"
                    >
                    <span v-else class="avatar-placeholder">
                      {{ (childComment.username || `用户${childComment.createBy}`).charAt(0).toUpperCase() }}
                    </span>
                  </div>
                  <div class="author-info">
                    <span class="username">
                      {{ childComment.username || `用户${childComment.createBy}` }}
                      <span v-if="childComment.toCommentUserName" class="reply-to">
                        回复 {{ childComment.toCommentUserName }}
                      </span>
                    </span>
                    <span class="comment-date">{{ formatDate(childComment.createTime) }}</span>
                  </div>
                </div>
              </div>
              <div class="comment-content">{{ childComment.content }}</div>
              <div class="comment-actions" v-if="replyingTo !== childComment.id">
                <button 
                  class="reply-btn" 
                  @click="toggleReply(childComment.id, comment.id)"
                  :class="{ active: replyingTo === childComment.id }"
                >
                  <span class="reply-icon">↩️</span>
                  回复
                </button>
              </div>
              
              <div v-if="replyingTo === childComment.id" class="reply-form">
                <div class="textarea-wrapper">
                  <textarea 
                    v-model="replyContent"
                    :placeholder="`回复 ${childComment.username || `用户${childComment.createBy}`}...`"
                    rows="3"
                    maxlength="300"
                  ></textarea>
                  <div class="char-count" :class="{ 'limit-warning': replyContent.length > 270 }">
                    {{ replyContent.length }}/300
                  </div>
                </div>
                <div class="reply-actions">
                  <button 
                    class="btn btn-primary" 
                    @click="submitChildReply(childComment, comment)" 
                    :disabled="submitting || !replyContent.trim() || replyContent.length < 2"
                  >
                    {{ submitting ? '提交中...' : '提交回复' }}
                  </button>
                  <button class="btn btn-ghost" @click="cancelReply">取消</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else-if="!loading && !error" class="no-comments">
      <div class="empty-icon">💭</div>
      <p class="empty-title">暂无评论</p>
      <p class="empty-subtitle">成为第一个发表评论的人吧！</p>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载评论中...</p>
    </div>
    
    <div v-if="error" class="error-state">
      <div class="error-icon">⚠️</div>
      <p>{{ error }}</p>
      <button class="btn btn-outline" @click="retryLoad">重新加载</button>
    </div>
    
    <div class="comment-form">
      <h4 class="form-title">
        发表评论
      </h4>
      <div class="textarea-wrapper">
        <textarea 
          v-model="newCommentContent"
          placeholder="分享你的想法..."
          rows="4"
          class="comment-textarea"
          maxlength="500"
        ></textarea>
        <div class="char-count" :class="{ 'limit-warning': newCommentContent.length > 450 }">
          {{ newCommentContent.length }}/500
        </div>
      </div>
      <div class="form-actions">
        <button 
          class="btn btn-primary" 
          @click="submitComment"
          :disabled="!newCommentContent.trim() || submitting || newCommentContent.length < 2"
        >
          <span v-if="submitting" class="loading-dot"></span>
          {{ submitting ? '提交中...' : '发布评论' }}
        </button>
      </div>
    </div>
    
    <PageForm 
      :currentPage="currentPage"
      :totalPages="totalPages"
      @page-change="loadComments"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { commentApi } from '@/api/comment'
import type { Comment } from '@/types'
import PageForm from './PageForm.vue'

const route = useRoute()

// 响应式数据
const comments = ref<Comment[]>([])
const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const newCommentContent = ref('')
const replyContent = ref('')
const replyingTo = ref<number | string | null>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalComments = ref(0)
const totalPages = ref(0)
const expandedComments = ref<Set<number | string>>(new Set()) // 跟踪哪些评论的子评论是展开的

// 计算属性
const articleId = computed(() => Number(route.params.id))

// 计算所有评论总数（包括子评论）
const allCommentsCount = computed(() => {
  let count = 0
  comments.value.forEach(comment => {
    count++ // 根评论
    if (comment.children) {
      count += comment.children.length // 子评论
    }
  })
  return count
})

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 一分钟内
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  // 一小时内
  if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`
  }
  // 一天内
  if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  }
  // 一周内
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (24 * 60 * 60 * 1000))}天前`
  }
  
  // 超过一周显示具体日期
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载评论列表
const loadComments = async (page: number = 1) => {
  try {
    loading.value = true
    error.value = ''
    const response = await commentApi.getCommentList(articleId.value, {
      page: page,
      pageSize: pageSize.value
    })
    
    comments.value = response.rows || []
    totalComments.value = typeof response.total === 'string' ? parseInt(response.total) : (response.total || 0)
    totalPages.value = Math.ceil(totalComments.value / pageSize.value)
    currentPage.value = page
  } catch (err) {
    console.error('加载评论失败:', err)
    error.value = '评论加载失败，请检查网络连接'
    comments.value = []
    totalComments.value = 0
  } finally {
    loading.value = false
  }
}

// 重新加载评论
const retryLoad = () => {
  loadComments(currentPage.value)
}

// 提交评论
const submitComment = async () => {
  if (!newCommentContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  
  if (newCommentContent.value.trim().length < 2) {
    alert('评论内容至少需要2个字符')
    return
  }
  
  if (newCommentContent.value.trim().length > 500) {
    alert('评论内容不能超过500个字符')
    return
  }
  
  try {
    submitting.value = true
    await commentApi.addComment({
      articleId: articleId.value,
      content: newCommentContent.value.trim(),
      rootId: 0
    })
    
    newCommentContent.value = ''
    // 提交成功后跳转到第一页显示最新评论
    await loadComments(1)
    
    // 滚动到评论区
    const commentsSection = document.querySelector('.comments-section')
    if (commentsSection) {
      commentsSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    alert('提交评论失败，请检查网络连接后重试')
  } finally {
    submitting.value = false
  }
}

// 切换回复状态
const toggleReply = (commentId: number | string, rootId?: number | string) => {
  if (replyingTo.value === commentId) {
    cancelReply()
  } else {
    replyingTo.value = commentId
    replyContent.value = ''
  }
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 切换子评论展开状态
const toggleExpand = (commentId: number | string) => {
  if (expandedComments.value.has(commentId)) {
    expandedComments.value.delete(commentId)
  } else {
    expandedComments.value.add(commentId)
  }
}

// 检查评论是否展开
const isExpanded = (commentId: number | string) => {
  return expandedComments.value.has(commentId)
}

// 提交对根评论的回复
const submitReply = async (parentComment: Comment) => {
  if (!replyContent.value.trim()) {
    alert('请输入回复内容')
    return
  }
  
  if (replyContent.value.trim().length < 2) {
    alert('回复内容至少需要2个字符')
    return
  }
  
  if (replyContent.value.trim().length > 300) {
    alert('回复内容不能超过300个字符')
    return
  }
  
  try {
    submitting.value = true
    await commentApi.addComment({
      articleId: articleId.value,
      content: replyContent.value.trim(),
      rootId: parentComment.id,
      toCommentId: parentComment.id,
      toCommentUserId: parentComment.createBy
    })
    
    replyContent.value = ''
    replyingTo.value = null
    // 重新加载评论列表
    await loadComments(currentPage.value)
    
    // 显示成功提示
    console.log('回复提交成功')
  } catch (error) {
    console.error('提交回复失败:', error)
    alert('提交回复失败，请检查网络连接后重试')
  } finally {
    submitting.value = false
  }
}

// 提交对子评论的回复
const submitChildReply = async (childComment: Comment, rootComment: Comment) => {
  if (!replyContent.value.trim()) {
    alert('请输入回复内容')
    return
  }
  
  if (replyContent.value.trim().length < 2) {
    alert('回复内容至少需要2个字符')
    return
  }
  
  if (replyContent.value.trim().length > 300) {
    alert('回复内容不能超过300个字符')
    return
  }
  
  try {
    submitting.value = true
    await commentApi.addComment({
      articleId: articleId.value,
      content: replyContent.value.trim(),
      rootId: rootComment.id,
      toCommentId: childComment.id,
      toCommentUserId: childComment.createBy
    })
    
    replyContent.value = ''
    replyingTo.value = null
    // 重新加载评论列表
    await loadComments(currentPage.value)
    
    // 显示成功提示
    console.log('回复提交成功')
  } catch (error) {
    console.error('提交回复失败:', error)
    alert('提交回复失败，请检查网络连接后重试')
  } finally {
    submitting.value = false
  }
}

// 组件挂载时加载评论
onMounted(() => {
  loadComments()
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

// 简约现代风格评论系统

.comments-section {
  margin-top: 48px;
  font-family: $font-family-sans-serif;
}

.comments-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 8px;
  
  .comment-icon {
    font-size: 1.2rem;
    opacity: 0.8;
  }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 40px;
}

.comment-wrapper {
  position: relative;
}

.comment-item {
  position: relative;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  transition: all 0.2s ease;
  //padding-bottom: 4rem; // 给底部回复按钮留出空间

  &:hover {
    background: rgba(255, 255, 255, 0.06);
    border-color: rgba(255, 255, 255, 0.12);
  }

  &.child-comment {
    margin-left: 32px;
    margin-top: 12px;
    background: rgba(255, 255, 255, 0.02);
    
    @media (max-width: 768px) {
      margin-left: 16px;
    }
  }
}

.comment-body {
  padding: 16px;
}

.children-comments {
  padding: 0 16px 16px;
}

.comment-header {
  margin-bottom: 12px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: $border-color;
  display: flex;
  align-items: center;
  justify-content: center;

  &.child-avatar {
    width: 32px;
    height: 32px;
  }

  .avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .avatar-placeholder {
    font-weight: 600;
    color: $text-color;
    font-size: 0.9rem;
    background: linear-gradient(135deg, $primary-color, $secondary-color);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 500;
  font-size: 0.95rem;
  color: $text-color;
  display: flex;
  align-items: center;
  gap: 8px;
}

.reply-to {
  font-size: 0.8rem;
  color: $text-color-secondary;
  font-weight: 400;
  padding: 2px 6px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 4px;
}

.comment-date {
  font-size: 0.8rem;
  color: $text-color-secondary;
  opacity: 0.7;
}

.comment-content {
  font-size: 0.95rem;
  line-height: 1.6;
  color: $text-color;
  margin: 12px 0 16px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.comment-actions {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  display: flex;
  gap: 12px;
}

.reply-btn {
  background: none;
  border: none;
  color: $text-color-secondary;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 400;

  &:hover {
    color: $primary-color;
    background: rgba($primary-color, 0.08);
  }

  &.active {
    color: $primary-color;
    background: rgba($primary-color, 0.12);
  }

  .reply-icon {
    font-size: 0.75rem;
  }
}

.expand-btn {
  background: none;
  border: none;
  color: $text-color-secondary;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 400;

  &:hover {
    color: $primary-color;
    background: rgba($primary-color, 0.08);
  }

  &.expanded {
    color: $primary-color;
    background: rgba($primary-color, 0.08);
  }

  .expand-icon {
    font-size: 0.7rem;
    transition: transform 0.2s ease;
  }
}

.reply-form {
  position: relative;
  margin-top: 16px;
  padding: 16px;
  padding-bottom: 60px; // 为底部按钮留出空间
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.06);

  .textarea-wrapper {
    position: relative;
    margin-bottom: 12px;
  }

  textarea {
    width: 100%;
    min-height: 80px;
    padding: 12px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.08);
    color: $text-color;
    border-radius: 4px;
    resize: vertical;
    font-family: $font-family-sans-serif;
    font-size: 0.9rem;
    line-height: 1.5;
    transition: border-color 0.2s ease;

    &:focus {
      outline: none;
      border-color: $primary-color;
    }

    &::placeholder {
      color: $text-color-secondary;
      opacity: 0.6;
    }
  }
}

.reply-actions {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  gap: 8px;

  @media (max-width: 768px) {
    position: static;
    margin-top: 12px;
    flex-direction: column;
  }
}

// 状态样式
.no-comments, .loading-state, .error-state {
  text-align: center;
  padding: 48px 24px;
  color: $text-color-secondary;
}

.no-comments {
  .empty-icon {
    font-size: 2.5rem;
    margin-bottom: 16px;
    opacity: 0.5;
  }

  .empty-title {
    font-size: 1.1rem;
    font-weight: 500;
    margin: 0 0 4px;
    color: $text-color;
  }

  .empty-subtitle {
    font-size: 0.9rem;
    margin: 0;
    opacity: 0.7;
  }
}

.loading-state {
  .loading-spinner {
    width: 24px;
    height: 24px;
    border: 2px solid rgba($primary-color, 0.3);
    border-top: 2px solid $primary-color;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 12px;
  }
}

.error-state {
  color: #ff6b6b;
  
  .error-icon {
    font-size: 2rem;
    margin-bottom: 12px;
  }
}

// 评论表单
.comment-form {
  background: rgba(255, 255, 255, 0.04);
  padding: 24px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 32px;
}

.form-title {
  font-size: 1.1rem;
  font-weight: 500;
  color: $text-color;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.textarea-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.comment-textarea {
  width: 100%;
  min-height: 100px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: $text-color;
  border-radius: 6px;
  resize: vertical;
  font-family: $font-family-sans-serif;
  font-size: 0.95rem;
  line-height: 1.6;
  transition: border-color 0.2s ease;

  &:focus {
    outline: none;
    border-color: $primary-color;
  }

  &::placeholder {
    color: $text-color-secondary;
    opacity: 0.6;
  }
}

.char-count {
  position: absolute;
  bottom: 8px;
  right: 12px;
  font-size: 0.75rem;
  color: $text-color-secondary;
  background: rgba($background-color, 0.8);
  padding: 2px 6px;
  border-radius: 3px;

  &.limit-warning {
    color: #ff6b6b;
    font-weight: 500;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

// 按钮样式
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &.btn-primary {
    background: $primary-color;
    color: white;

    &:hover:not(:disabled) {
      background: lighten($primary-color, 8%);
    }
  }

  &.btn-ghost {
    background: rgba(255, 255, 255, 0.06);
    color: $text-color-secondary;
    border: 1px solid rgba(255, 255, 255, 0.12);

    &:hover:not(:disabled) {
      background: rgba(255, 255, 255, 0.1);
      color: $text-color;
    }
  }

  &.btn-outline {
    background: transparent;
    color: $primary-color;
    border: 1px solid $primary-color;

    &:hover:not(:disabled) {
      background: rgba($primary-color, 0.1);
    }
  }
}

.loading-dot {
  width: 12px;
  height: 12px;
  border: 1.5px solid rgba(255, 255, 255, 0.3);
  border-top: 1.5px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 响应式设置
@media (max-width: 768px) {
  .comments-section {
    margin-top: 32px;
  }

  .comments-title {
    font-size: 1.3rem;
    margin-bottom: 24px;
  }

  .comment-item .comment-body {
    padding: 12px;
  }

  .children-comments {
    padding: 0 12px 12px;
  }

  .comment-form {
    padding: 16px;
  }

  .comment-textarea {
    min-height: 80px;
    padding: 12px;
    font-size: 0.9rem;
  }

  .btn {
    padding: 6px 12px;
    font-size: 0.85rem;
  }

  .user-avatar {
    width: 32px;
    height: 32px;

    &.child-avatar {
      width: 28px;
      height: 28px;
    }
  }
}
</style>