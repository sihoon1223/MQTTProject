import React, { Component } from 'react';

//CPU, Memory, Network, ConnectionInfo 차트에서 한 화면당 보이는
//data의 개수와 (dateLength)
//update되는 주기를 지정 (updateInterval)

const withSplitting = getComponent => {
  // 여기서 getComponent 는 () => import('./SplitMe') 의 형태로 함수가 전달되어야
  class WithSplitting extends Component {
    static Splitted = null; // 기본값은 null 이지만
    static preload() {
      // preload 가 호출되면 위 static Splitted 가 설정되고
      getComponent().then(({ default: Splitted }) => {
        WithSplitting.Splitted = Splitted;
      });
    }
    state = {
      Splitted: WithSplitting.Splitted // 컴포넌트가 생성되는 시점에서 static Splitted 를 사용하게 되므로 null 이나 컴포넌트를 사용하게됨
    };

    constructor(props) {
      super(props);
      getComponent().then(({ default: Splitted }) => {
        this.setState({
          Splitted
        });
      });
    }

    render() {
      const { Splitted } = this.state;
      if (!Splitted) {
        return null;
      }
      return <Splitted {...this.props} dataLength={25} updateInterval={5} />;
    }
  }
  return WithSplitting;
};

export default withSplitting;
