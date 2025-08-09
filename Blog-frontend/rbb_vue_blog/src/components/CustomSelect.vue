<template>
  <div class="custom-select">
    <div class="select-trigger" @click="toggleDropdown" :class="{ active: dropdownOpen }">
      <slot name="trigger" :selected-option="getSelectedOption()" :toggle-dropdown="toggleDropdown">
        <span class="select-value">{{ getOptionLabel(modelValue) }}</span>
        <span class="select-arrow" :class="{ rotated: dropdownOpen }">▼</span>
      </slot>
    </div>
    <div class="select-dropdown" v-show="dropdownOpen">
      <div 
        v-for="option in options" 
        :key="option.value"
        class="select-option" 
        :class="{ selected: modelValue === option.value }"
        @click="selectOption(option.value)"
      >
        <span v-if="option.icon" class="option-icon">{{ option.icon }}</span>
        {{ option.label }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface SelectOption {
  value: string | number
  label: string
  icon?: string
}

interface Props {
  modelValue: string | number
  options: SelectOption[]
  placeholder?: string
}

interface Emits {
  (e: 'update:modelValue', value: string | number): void
  (e: 'change', value: string | number): void
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请选择'
})

const emit = defineEmits<Emits>()

const dropdownOpen = ref(false)

// 切换下拉菜单
const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

// 选择选项
const selectOption = (value: string | number) => {
  emit('update:modelValue', value)
  emit('change', value)
  dropdownOpen.value = false
}

// 获取选项标签
const getOptionLabel = (value: string | number) => {
  const option = props.options.find(opt => opt.value === value)
  return option?.label || props.placeholder
}

// 获取选中的选项对象
const getSelectedOption = () => {
  return props.options.find(opt => opt.value === props.modelValue)
}

// 点击外部关闭下拉菜单
const closeDropdown = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.custom-select')) {
    dropdownOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', closeDropdown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeDropdown)
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.custom-select {
  position: relative;
  min-width: 160px;
}

.select-trigger {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
  color: $text-color;
  font-size: 0.9rem;

  &:hover {
    background: rgba(255, 255, 255, 0.06);
    border-color: rgba($primary-color, 0.5);
  }

  &.active {
    border-color: $primary-color;
    background: rgba($primary-color, 0.1);
  }
}

.select-arrow {
  transition: transform 0.3s ease;
  font-size: 0.8rem;
  color: $text-color-secondary;

  &.rotated {
    transform: rotate(180deg);
    color: $primary-color;
  }
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  margin-top: 4px;
  overflow: hidden;
  z-index: 1000;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.select-option {
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  color: $text-color;
  font-size: 0.9rem;

  &:hover {
    background: rgba(255, 255, 255, 0.08);
    color: $primary-color;
  }

  &.selected {
    background: rgba($primary-color, 0.15);
    color: $primary-color;
    font-weight: 500;
  }

  .option-icon {
    font-size: 0.85rem;
    opacity: 0.8;
  }
}
</style>
