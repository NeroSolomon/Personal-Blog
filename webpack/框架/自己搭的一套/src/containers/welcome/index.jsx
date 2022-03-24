import React from 'react';
import { Layout } from 'antd';
import Cookies from 'js-cookie'
import styles from './index.module.less';

const { Content, Sider } = Layout;

class Welcome extends React.Component {
  constructor(props) {
    super(props);
  }

  UNSAFE_componentWillMount() {
    // 只使用 v2 token认证
    const token = Cookies.get('ACCESS_TOKEN');
    // 如果没有token需要登录
    if (!token) {
      this.redirectLogin();
    }
  }

  redirectLogin = () => {
    window.location.href = `/login?next=/${window.encodeURIComponent(
      location.search
    )}`;
  };

  render() {
    return (
      <Layout className={styles['content']}>
        <Sider></Sider>
        <Layout>
          <Content></Content>
        </Layout>
      </Layout>
    );
  }
}

export default Welcome;
