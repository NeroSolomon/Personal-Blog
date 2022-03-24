import React from 'react'
import { Tooltip, Button } from 'antd'

const TooltipButton = props => {
  const { children, tip, placement, ...restProps } = props
  return (
    <Tooltip title={tip} placement={placement}>
      <Button {...restProps}>{children}</Button>
    </Tooltip>
  )
}

export default TooltipButton