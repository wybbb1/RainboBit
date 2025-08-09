<template>
  <div class="about-page">
    <!-- 背景图片容器 -->
    <div class="background-container" :class="{ 'background-loaded': backgroundLoaded }">
      <!-- 头像容器 -->
      <div class="avatar-container">
        <div class="avatar animate-fade-up">
          <img src="@/assets/images/avatar.jpg" alt="头像" />
        </div>
        <h2 class="name animate-fade-up">RainBot</h2>
        <p class="subtitle animate-fade-up">全栈开发者 | 技术爱好者</p>

        <!-- 联系方式按钮 - 水平排列 -->
        <div class="contact-buttons-row animate-fade-up">
          <!-- 邮箱按钮 -->
          <div class="contact-button-inline email-button" @click="openContact('email')" :title="contactInfo.email.title">
            <img :src="contactInfo.email.icon" alt="邮箱" />
          </div>

          <!-- 微信按钮 -->
          <div class="contact-button-inline wechat-button" @click="openContact('wechat')" :title="contactInfo.wechat.title">
            <img :src="contactInfo.wechat.icon" alt="微信" />
          </div>

          <!-- GitHub按钮 -->
          <div class="contact-button-inline github-button" @click="openContact('github')" :title="contactInfo.github.title">
            <img :src="contactInfo.github.icon" alt="GitHub" />
          </div>

          <!-- CSDN按钮 -->
          <div class="contact-button-inline csdn-button" @click="openContact('csdn')" :title="contactInfo.csdn.title">
            <img :src="contactInfo.csdn.icon" alt="CSDN" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// 导入背景图片
import backgroundImage from '@/assets/images/background.jpg'

// 导入图标
import emailIcon from '@/assets/icon/email.svg'
import wechatIcon from '@/assets/icon/wechat.svg'
import githubIcon from '@/assets/icon/github.svg'
import csdnIcon from '@/assets/icon/csdn.svg'

const router = useRouter()

// 背景图片加载状态
const backgroundLoaded = ref(false)

// 联系方式信息
const contactInfo = {
  email: {
    title: '邮箱',
    value: '958873999@qq.com',
    link: 'mailto:958873999@qq.com',
    icon: emailIcon
  },
  wechat: {
    title: '微信',
    value: 'wyybbbbbbb_',
    link: '#',
    icon: wechatIcon
  },
  github: {
    title: 'GitHub',
    value: 'wybbb1',
    link: 'https://github.com/wybbb1',
    icon: githubIcon
  },
  csdn: {
    title: 'CSDN',
    value: 'Ra1nbot_',
    link: 'https://blog.csdn.net/2302_79210819?spm=1000.2115.3001.5343',
    icon: csdnIcon
  }
}

// 打开联系方式
const openContact = (type: keyof typeof contactInfo) => {
  const contact = contactInfo[type]
  
  if (type === 'wechat') {
    // 微信特殊处理，可以显示二维码或复制微信号
    navigator.clipboard.writeText(contact.value).then(() => {
      alert(`微信号 ${contact.value} 已复制到剪贴板`)
    }).catch(() => {
      alert(`微信号 ${contact.value}`)
    })
  } else {
    // 其他联系方式直接打开链接
    window.open(contact.link, '_blank')
  }
}

// 返回上一页或首页
// 已删除goBack函数，因为移除了关闭按钮

// 设置背景图片
onMounted(() => {
  // 预加载背景图片，用于触发动画
  const img = new Image()
  img.onload = () => {
    backgroundLoaded.value = true
  }
  img.src = backgroundImage
})
</script>

<style lang="scss" scoped>
.about-page {
  // 突破容器限制，实现真正的全屏
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  z-index: 100; // 降低z-index，避免与其他元素冲突
}

.background-container {
  position: relative;
  height: 100%;
  width: 100%;
  // 直接设置背景图片和渐变，让浏览器自动预加载
  background: url('@/assets/images/background.jpg');
  background-size: cover; // 使用cover确保背景图片完全覆盖容器
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed; // 固定背景，避免滚动时的问题
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding-left: 8%;
  transition: all 0.3s ease-in-out;

  // 背景图片加载完成后的样式
  &.background-loaded {
    // 添加背景图片的轻微浮动效果，不改变尺寸
    animation: backgroundFloat 8s ease-in-out infinite;
  }

}

.avatar-container {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  
  .avatar {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    overflow: hidden;
    margin: 0 auto 30px;
    border: 6px solid rgba(255, 255, 255, 1); // 改为完全不透明的白色边框
    box-shadow: 0 10px 30px rgba(0, 0, 0, 1);
    transition: transform 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
    }
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
      opacity: 1; // 确保头像图片完全不透明
    }
  }
  
  .name {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 10px;
    color: white;
    -webkit-text-stroke: 1px rgba(0, 0, 0, 0.3);
  }
  
  .subtitle {
    font-size: 1.2rem;
    margin: 0 0 40px 0;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
    
    // 只有没有动画类的时候才显示
    &:not(.animate-fade-up) {
      opacity: 0.9;
    }
  }
}

// 动态进入动画
.animate-fade-up {
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 0.8s ease-out forwards;
}

// 为不同元素设置不同的延迟
.avatar.animate-fade-up { animation-delay: 0.2s; }
.name.animate-fade-up { animation-delay: 0.4s; }
.subtitle.animate-fade-up { animation-delay: 0.6s; }
.contact-buttons-row.animate-fade-up { animation-delay: 0.8s; }

@keyframes fadeUp {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  60% {
    opacity: 0.8;
    transform: translateY(-5px) scale(1.02);
  }
  100% {
    opacity: 0.9; // 副标题的最终透明度
    transform: translateY(0) scale(1);
  }
}

// 水平排列的联系方式按钮
.contact-buttons-row {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 20px;
}

.contact-button-inline {
  width: 50px;
  height: 50px;
  backdrop-filter: blur(10px);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  color: white;
  position: relative;
  overflow: hidden;

  // 添加轻微的浮动动画
  animation: float 3s ease-in-out infinite;
  
  // 为每个按钮添加不同的动画延迟（在进入动画完成后）
  &:nth-child(1) { animation-delay: 1.2s; }
  &:nth-child(2) { animation-delay: 1.4s; }
  &:nth-child(3) { animation-delay: 1.6s; }
  &:nth-child(4) { animation-delay: 1.8s; }
  
  img {
    width: 24px;
    height: 24px;
    object-fit: contain;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    filter: brightness(0) invert(1);
    z-index: 1;
  }
  
  &:hover {
    animation-play-state: paused;
  }
  
  &:hover img {
    transform: scale(1.2) rotate(5deg);
  }
  
  // 点击效果
  &:active {
    transform: translateY(-4px) scale(0.95);
    transition: all 0.1s ease;
  }
  
  // 邮箱按钮 - 柔和蓝色 + 弹跳效果
  &.email-button {
    background: #63b3ed; // 改为不透明
    border: 2px solid #63b3ed;
    
    &:hover {
      background: #4299e1; // 悬停时稍微深一点
      transform: translateY(-12px) scale(1.08);
      box-shadow: 0 20px 40px rgba(99, 179, 237, 0.5);
      border-color: #4299e1;
      animation: bounce 0.6s ease;
    }
  }

  // 微信按钮 - 柔和薄荷绿 + 摇摆效果
  &.wechat-button {
    background: #48bb78; // 改为不透明
    border: 2px solid #48bb78;
    
    &:hover {
      background: #38a169; // 悬停时稍微深一点
      transform: translateY(-8px) scale(1.05) rotate(-2deg);
      box-shadow: 0 15px 35px rgba(72, 187, 120, 0.5);
      border-color: #38a169;
      animation: wiggle 0.5s ease;
    }
  }
  
  // GitHub按钮 - 柔和紫色 + 平滑缩放效果
  &.github-button {
    background: #9f7aea; // 改为不透明
    border: 2px solid #9f7aea;
    
    &:hover {
      background: #805ad5; // 悬停时稍微深一点
      transform: translateY(-10px) scale(1.08);
      box-shadow: 0 18px 40px rgba(159, 122, 234, 0.5);
      border-color: #805ad5;
    }
  }

  // CSDN按钮 - 柔和珊瑚色 + 旋转效果
  &.csdn-button {
    background: #fb923c; // 改为不透明
    border: 2px solid #fb923c;
    
    &:hover {
      background: #ed8936; // 悬停时稍微深一点
      transform: translateY(-8px) scale(1.05) rotate(8deg);
      box-shadow: 0 15px 35px rgba(251, 146, 60, 0.5);
      border-color: #ed8936;
      animation: spin-gentle 1s ease;
    }
  }
}

// 关键帧动画定义
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-6px);
  }
}

// 背景图片动画 - 只改变位置，不改变尺寸
@keyframes backgroundFloat {
  0% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
  20% {
    background-position: 48% 52%;
    transform: translateX(3px) translateY(-2px);
  }
  40% {
    background-position: 52% 48%;
    transform: translateX(-2px) translateY(3px);
  }
  60% {
    background-position: 49% 53%;
    transform: translateX(4px) translateY(1px);
  }
  80% {
    background-position: 51% 47%;
    transform: translateX(-3px) translateY(-2px);
  }
  100% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
}

// 移动端背景动画 - 减少晃动幅度
@keyframes backgroundFloatMobile {
  0% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
  20% {
    background-position: 49% 51%;
    transform: translateX(1px) translateY(-1px);
  }
  40% {
    background-position: 51% 49%;
    transform: translateX(-1px) translateY(1px);
  }
  60% {
    background-position: 49.5% 51.5%;
    transform: translateX(1.5px) translateY(0.5px);
  }
  80% {
    background-position: 50.5% 48.5%;
    transform: translateX(-1px) translateY(-0.5px);
  }
  100% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
}

// 小屏幕背景动画 - 最小晃动幅度
@keyframes backgroundFloatSmall {
  0% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
  25% {
    background-position: 49.5% 50.5%;
    transform: translateX(0.5px) translateY(-0.5px);
  }
  50% {
    background-position: 50.5% 49.5%;
    transform: translateX(-0.5px) translateY(0.5px);
  }
  75% {
    background-position: 49.5% 50.5%;
    transform: translateX(0.5px) translateY(0px);
  }
  100% {
    background-position: 50% 50%;
    transform: translateX(0px) translateY(0px);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(-12px) scale(1.08);
  }
  40% {
    transform: translateY(-16px) scale(1.1);
  }
  60% {
    transform: translateY(-10px) scale(1.06);
  }
}

@keyframes wiggle {
  0%, 100% {
    transform: translateY(-8px) scale(1.05) rotate(-2deg);
  }
  25% {
    transform: translateY(-8px) scale(1.05) rotate(2deg);
  }
  50% {
    transform: translateY(-8px) scale(1.05) rotate(-1deg);
  }
  75% {
    transform: translateY(-8px) scale(1.05) rotate(1deg);
  }
}

@keyframes spin-gentle {
  0% {
    transform: translateY(-8px) scale(1.05) rotate(8deg);
  }
  25% {
    transform: translateY(-8px) scale(1.05) rotate(12deg);
  }
  50% {
    transform: translateY(-8px) scale(1.05) rotate(8deg);
  }
  75% {
    transform: translateY(-8px) scale(1.05) rotate(4deg);
  }
  100% {
    transform: translateY(-8px) scale(1.05) rotate(8deg);
  }
}

// 响应式设置
@media (max-width: 768px) {
  .background-container {
    padding-left: 4%;
    justify-content: center;
    
    // 在小屏幕上减少背景动画幅度
    &.background-loaded {
      animation: backgroundFloatMobile 8s ease-in-out infinite;
    }
  }
  
  .avatar-container {
    .avatar {
      width: 150px;
      height: 150px;
    }
    
    .name {
      font-size: 2rem;
    }
    
    .subtitle {
      font-size: 1rem;
      margin-bottom: 30px;
    }
  }
  
  .contact-buttons-row {
    gap: 15px;
  }
  
  .contact-button-inline {
    width: 45px;
    height: 45px;
    
    img {
      width: 20px;
      height: 20px;
    }
  }
}

@media (max-width: 480px) {
  .background-container {
    // 在超小屏幕上进一步减少动画幅度
    &.background-loaded {
      animation: backgroundFloatSmall 8s ease-in-out infinite;
    }
  }
  
  .avatar-container {
    .avatar {
      width: 120px;
      height: 120px;
    }
    
    .name {
      font-size: 1.8rem;
    }
    
    .subtitle {
      font-size: 0.9rem;
      margin-bottom: 25px;
    }
  }
  
  .contact-buttons-row {
    gap: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .contact-button-inline {
    width: 40px;
    height: 40px;
    
    img {
      width: 18px;
      height: 18px;
    }
  }
}
</style>
