import React, { Component, Fragment } from 'react';
import { Col, Row, Container } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//cpu chart component

class CPU extends Component {
  chartData = []; //차트 데이터
  dataLength = this.props.dataLength;  //x축에 표시될 데이터 개수
  updateInterval = this.props.updateInterval; //화면 갱신 interval

  /*constructor(props) {
    super(props);
  }*/

  componentDidMount() { //컴포넌트가 화면에 나타나게 되었을 때 호출
    console.log("cpu did mount");
    
    //차트 생성, 초기화
    makeChart.create("cpu", "cpuChartHolder", makeChart.cpuLayoutStr, this.chartData);
  }

  componentWillUnmount() {  //컴포넌트가 제거될 때 호출
    console.log("cpu unmount");
  }

  //this.props.chartData로 Perform(부모)컴포넌트에서 subscribe된 차트데이터 갱신
  //index가 작을수록 최근 데이터
  componentWillReceiveProps(nextProps) {  //컴포넌트가 새로운 props를 받게 되었을 때 호출
    if (this.props.chartData !== nextProps.chartData) {
      console.log("receive props");
      
      console.log(this.props.chartData);
      this.messageArrived(this.props.chartData);
      //this.props.chartData.map((message, i) => i===0 && (this.messageArrived(message))); //가장 최근에 도착한 메시지를 파라미터로 받는 messageArrived 함수 호출
    }
  }

  // "데이터개수;cpu_util;date;..."
  messageArrived(data) {  //차트 데이터 갱신
    this.chartData = []; //기존 차트 데이터 삭제, 업데이트 될 차트 데이터로 갱신
    
    let dataArray = data.split(';');
    let dataLength = dataArray[0];
    let cpuUtil = [];
    let date = [];
    for(let i=1; i<(dataArray.length-1)/2 + 1; i++) {
      cpuUtil.push(dataArray[i*2 - 1]);
      date.push(dataArray[i*2]);
    }
    //console.log(dataLength);
    //console.log(cpuUtil);
    //console.log(date);
    
    if(dataLength === 0 || dataLength === "") {  //db가 비어있는 경우
      this.initData(0);
    }
    else if(dataLength < this.dataLength) { //db의 차트 데이터가 x축 항 개수보다 작은 경우
      this.initData(dataLength);

      this.updateData(dataLength - 1, date, cpuUtil);
      this.updateData(0, date, cpuUtil);
    }
    else { 
      this.updateData(this.dataLength - 1, date, cpuUtil);
      this.updateData(0, date, cpuUtil);
    }

    console.log(this.chartData);
    document.getElementById("cpu").setData(this.chartData); //newData로 차트 갱신
  }

  initData(n) {
    for(let i=0; i<this.dataLength - n; i++) {
      this.chartData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), 
        "date":"", "totalCpu":0});
    }
  }

  updateData(n, date, cpuUtil) {
    if(n === 0 || n === 1) { //가장 최근 데이터의 x축값은 date
      this.chartData.push({"xValue":date[0], "date":date[0], "totalCpu":parseFloat(cpuUtil[0], 10)});
    }
    else {
      for(let i=0; i<n; i++) {
        this.chartData.push({"xValue":(this.updateInterval)*(n - i), 
          "date":date[n - i], "totalCpu":parseFloat(cpuUtil[n - i], 10)});
        }

    }
  }

  render() {
    const style = {
      width:'44vw',
      height:'42vh',
      background:'white',
      borderRadius: '25px',
      padding:'2%',
      margin:'1%',
      //marginLeft:'1%',
      display:'inline-block',
      float:'left',
      position:'absolute',
      top:'0vh',
      left:'1vw'
      //boxShadow: 'inset -2px 0 0 rgba(0, 0, 0, .1)',
    };

    return (
      <Fragment>
        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <div>
          <div style={style} id="cpuChartHolder"/>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default CPU;
