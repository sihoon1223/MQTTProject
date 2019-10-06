import React, { Component, Fragment } from 'react';
import { Col, Row, Container } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//memory chart component

class Memory extends Component {
  chartData = []; //초기 차트 데이터
  dataLength = this.props.dataLength;  //x축에 표시될 데이터 개수
  updateInterval = this.props.updateInterval; //화면 갱신 interval

  /*constructor(props) {
    super(props);
  }*/

  componentDidMount() {
    console.log("memory did mount");
    
    //차트 생성, 초기화
    makeChart.create("memory", "memoryChartHolder", makeChart.memoryLayoutStr1 + makeChart.memoryLayoutStr2, this.chartData);
  }

  componentWillUnmount() {
    console.log("memory unmount");
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.chartData !== nextProps.chartData) {
      console.log("receive props");

      //this.props.chartData.map((message, i) => i===0 && this.messageArrived(message));
      this.messageArrived(this.props.chartData);
    }
  }

  // "totalMemory;데이터개수;memory_usage;date;..."
  messageArrived(data) {
    //console.log(data);
    this.chartData = []; //기존 차트 데이터 삭제, 업데이트 될 차트 데이터로 갱신

    let dataArray = data.split(';');
    let totalMemory = dataArray[0];
    let dataLength = dataArray[1];
    let memory = [];
    let date = [];
    for(let i=1; i<(dataArray.length-2)/2 + 1; i++) {
      memory.push(dataArray[i*2]);
      date.push(dataArray[i*2 + 1]);
    }
    //console.log(dataLength);
    //console.log(memory);
    //console.log(date);

    if(dataLength === 0 || dataLength === "") {
      this.initData(0);
    }
    else if(dataLength < this.dataLength) {
      this.initData(dataLength);

      this.updateData(dataLength - 1, date, memory);
      this.updateData(0, date, memory);
    }
    else {
      this.updateData(this.dataLength - 1, date, memory);
      this.updateData(0, date, memory);
    }

    console.log(this.chartData);
    document.getElementById("memory").setData(this.chartData); //newData로 차트 갱신

    //totalMemory로 차트 y축 최댓값을 지정
    let layoutStr = makeChart.memoryLayoutStr1 + 'maximum="' + totalMemory + '"' + makeChart.memoryLayoutStr2;
    document.getElementById("memory").setLayout(layoutStr);
  }

  initData(n) {
    for(let i=0; i<this.dataLength - n; i++) {
      this.chartData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), 
        "date":"", "memory":0});
    }
  }

  updateData(n, date, memory) {
    if(n === 0 || n === 1) { //가장 최근 데이터의 x축값은 date
      this.chartData.push({"xValue":date[0], "date":date[0], "memory":parseInt(memory[0], 10)});
    }
    else {
      for(let i=0; i<n; i++) {
        this.chartData.push({"xValue":(this.updateInterval)*(n - i), 
          "date":date[n - i], "memory":parseInt(memory[n - i], 10)});
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
      marginRight:'1vw',
      display:'inline-block',
      float:'left',
      position:'absolute',
      top:'0vh',
      left:'47vw'
      //boxShadow: 'inset -2px 0 0 rgba(0, 0, 0, .1)',
    };
    return (
      <Fragment>

        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <div>
        <div style={style} id="memoryChartHolder" />
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default Memory;
