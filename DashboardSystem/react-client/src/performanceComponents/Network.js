import React, { Component, Fragment } from 'react';
import { Col, Row, Container } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//network chart component

class Network extends Component {
  chartData = []; //초기 차트 데이터
  dataLength = this.props.dataLength;  //x축에 표시될 데이터 개수
  updateInterval = this.props.updateInterval; //화면 갱신 interval

  /*constructor(props) {
    super(props);
  }*/

  componentDidMount() {
    console.log("network did mount");

    //차트 생성, 초기화
    makeChart.create("network", "networkChartHolder", makeChart.networkLayoutStr, this.chartData);
  }

  componentWillUnmount() {
    console.log("network unmount");
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.chartData !== nextProps.chartData) {
      console.log("receive props");

      this.messageArrived(this.props.chartData);
      //this.props.chartData.map((message, i) => i===0 && this.messageArrived(message));
    }
  }

  //"데이터개수;network_in;network_out;date;..."
  messageArrived(data) {
    this.chartData = []; //기존 차트 데이터 삭제, 업데이트 될 차트 데이터로 갱신

    let dataArray = data.split(';');
    let dataLength = dataArray[0];
    let netIn = [];
    let netOut = [];
    let date = [];
    for(let i=1; i<(dataArray.length-1)/3 + 1; i++) {
      netIn.push(dataArray[i*3 - 2]);
      netOut.push(dataArray[i*3 - 1]);
      date.push(dataArray[i*3]);
    }
    //console.log(netIn);
    //console.log(netOut);
    //console.log(date);

    if(dataLength === 0 || dataLength === "") {
      this.initData(0);
    }
    else if(dataLength < this.dataLength) {
      this.initData(dataLength);

      this.updateData(dataLength - 1, date, netIn, netOut);
      this.updateData(0, date, netIn, netOut);
    }
    else {
      this.updateData(this.dataLength - 1, date, netIn, netOut);
      this.updateData(0, date, netIn, netOut);
    }

    console.log(this.chartData);
    document.getElementById("network").setData(this.chartData); //newData로 차트 갱신
  }

  initData(n) {
    for(let i=0; i<this.dataLength - n; i++) {
      this.chartData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), 
        "date":"", "in":0, "out":0});
    }
  }

  updateData(n, date, netIn, netOut) {
    if(n === 0 || n === 1) { //가장 최근 데이터의 x축값은 date
      this.chartData.push({"xValue":date[0], "date":date[0], "in":parseFloat(netIn[0], 10), "out":parseFloat(netOut[0], 10)});
    }
    else {
      for(let i=0; i<n; i++) {
        this.chartData.push({"xValue":(this.updateInterval)*(n - i), 
          "date":date[n - i], "in":parseFloat(netIn[n - i], 10), "out":parseFloat(netOut[n - i], 10)});
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
      top:'45vh',
      left:'1vw'
      //boxShadow: 'inset -2px 0 0 rgba(0, 0, 0, .1)',
    };

    return (
      <Fragment>

        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <div>
        <div style={style} id="networkChartHolder" />
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default Network;
