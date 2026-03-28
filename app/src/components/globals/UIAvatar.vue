<script>
export default {
  name: 'UIAvatar',
  props: {
    username: {
      type: String,
      default: 'User Name'
    },
    size: {
      type: String,
      default: 'md',
      validator: (value) => ['sm', 'md', 'lg'].includes(value)
    }
  },
  computed: {
    initials() {
      return this.username
        .split(' ')
        .map(word => word[0])
        .join('')
        .toUpperCase()
        .slice(0, 2);
    }
  }
}
</script>

<template>
  <div
    class="avatar"
    :class="size"
    :title="username"
  >
    <span>{{ initials }}</span>
  </div>
</template>

<style scoped>
.avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(56, 101, 242, 0.1);
  border: 2px solid rgba(56, 101, 242, 0.2);
  color: #3865F2;
  font-weight: 600;
  font-family: 'Montserrat', sans-serif;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.avatar:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(56, 101, 242, 0.15);
  border-color: rgba(56, 101, 242, 0.3);
}

.sm {
  width: 32px;
  height: 32px;
  border-radius: 16px;
  font-size: 12px;
}

.md {
  width: 40px;
  height: 40px;
  border-radius: 20px;
  font-size: 14px;
}

.lg {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
}
</style>
