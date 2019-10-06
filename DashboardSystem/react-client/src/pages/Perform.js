import React, { Component, Fragment } from 'react';
import { subscribe } from 'mqtt-react';
import { Col, Row, Container } from 'react-bootstrap';
import { CPU, Cores, Memory, Network, ProcessTable } from '../performanceComponents';

//topic:'performance/#'로 subscribe
//하위 컴포넌트들은 topic:'performance/성능' 으로 publish
//cpu, memory, network, process, mosquitto 성능

class Perform extends Component {
  constructor(props) {
    super(props);
    this.state = {
        cpuChartData: "",
        coresChartData:"",
        memoryChartData: "",
        networkChartData: "",
        processTableData: ""
    }
  }
  

  componentDidMount() { //컴포넌트가 화면에 나타나게 되었을 때 호출
    const { mqtt } = this.props;
    mqtt.publish('topic', 'performance did mount');

    console.log("performance did mount");
    console.log("connected : " + mqtt.connected);

    mqtt.on('message', function(topic, message){
        //console.log(topic.toString() + ", " + message.toString());

        if(topic.toString().split('/')[1] === 'cpu') {
            this.setState({
                cpuChartData: message.toString()
            }, function () {
                //console.log(this.state.cpuChartData);
            });
        }
        /*else if(topic.toString().split('/')[1] === 'cores') {
            this.setState({
                coresChartData: message.toString()
            }, function () {
                console.log("***");
                console.log(this.state.coresChartData);
            });
        }*/
        else if(topic.toString().split('/')[1] === 'memory') {
            this.setState({
                memoryChartData: message.toString()
            }, function () {
                //console.log(this.state.memoryChartData);
            });
        }
        else if(topic.toString().split('/')[1] === 'network') {
            this.setState({
                networkChartData: message.toString()
            }, function () {
                //console.log(this.state.networkChartData);
            });
        }
        else if(topic.toString().split('/')[1] === 'process') {
            this.setState({
                processTableData: message.toString()
            }, function () {
                //console.log(this.state.processTableData);
            });
        }

    }.bind(this));
  }

  componentWillUnmount() {  //컴포넌트가 제거될 때 호출
    const { mqtt } = this.props;
    mqtt.publish('topic', 'unmount');

    /*
    //컴포넌트 제거 시 unsubscribe
    mqtt.unsubscribe('cpu', function() {
        console.log('unsubscribe');
    });*/  

    console.log("unmount connected : " + mqtt.connected);
    console.log("performance unmount");
  }

  i = 0;
  // "데이터개수;cpu_util;date;..."
  // 차트 데이터 갱신
  componentWillReceiveProps(nextProps) {  //컴포넌트가 새로운 props를 받게 되었을 때 호출
    if (this.props.data !== nextProps.data) {
        console.log("performance receive props" + this.i);
        this.i++;
        //this.props.data.map((message, i) => i===0 && (this.messageArrived(message))); //가장 최근에 도착한 메시지를 파라미터로 받는 messageArrived 함수 호출
        //let temp = [];
        //const { mqtt } = this.props;
        /*mqtt.on('message', function(topic, message){
            console.log(topic.toString() + ", " + message.toString());

            if(topic.toString().split('/')[1] === 'cpu') {
                //temp.push(message.toString());
                this.setState({
                    cpuChartData: message.toString()
                });
            }

        }.bind(this));*/
    }
  }

  render() {

    return (
      <div>

        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <CPU chartData={this.state.cpuChartData}/>
        {/*<Cores chartData={this.state.coresChartData}/>*/}
        <Memory chartData={this.state.memoryChartData}/>
        <Network chartData={this.state.networkChartData}/>
        <ProcessTable tableData={this.state.processTableData}/>

        </Col>
        </Row>
        </Container>
      </div>
    );
  }
}

export default subscribe({
  topic: 'performance/#',
})(Perform)
