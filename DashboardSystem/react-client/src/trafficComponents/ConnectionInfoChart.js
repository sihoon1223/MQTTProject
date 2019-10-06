import React, { Component, Fragment } from 'react';
import { Col, Row, Container } from 'react-bootstrap';
import { Helmet } from "react-helmet";
import makeChart from '../js/MakeChart';

//connection chart component

class ConnectionInfoChart extends Component {
  chartData = []; //초기 차트 데이터
  dataLength = this.props.dataLength;  //x축에 표시될 데이터 개수
  updateInterval = this.props.updateInterval; //화면 갱신 interval

  constructor(props) {
    super(props)
  }

  componentDidMount() {
    console.log("connection did mount");

    //차트 생성, 초기화
    makeChart.create("connection", "connectionInfoChartHolder", makeChart.connectionLayoutStr, this.chartData);
  }

  componentWillUnmount() {
    console.log("connection unmount");
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.chartData !== nextProps.chartData) {
      console.log("receive props");
      
      //console.log(this.props.chartData);
      this.messageArrived(this.props.chartData);
      //this.props.chartData.map((message, i) => i===0 && (this.messageArrived(message))); //가장 최근에 도착한 메시지를 파라미터로 받는 messageArrived 함수 호출
    }
  }

  // "데이터개수;connections;recent;old;minimum;maximum;date..."로 publish
  messageArrived(data) {
    let dataArray = data.split(';');
    let dataLength = dataArray[0];
    let connections = [];
    let recent = [];
    let old = [];
    let minimum = [];
    let maximum = [];
    let date = [];

    for(let i=1; i<(dataArray.length-1)/6 + 1; i++) {
      connections.push(dataArray[i*6 - 5]);
      recent.push(dataArray[i*6 - 4]);
      old.push(dataArray[i*6 - 3]);
      minimum.push(dataArray[i*6 - 2]);
      maximum.push(dataArray[i*6 - 1]);
      date.push(dataArray[i*6]);
    }


    let newData = [];  //업데이트 될 차트 데이터
    if(dataLength === 0 || dataLength === "") {
      for(let i=0; i<this.dataLength - dataLength; i++) {
        newData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), "date":"",
          "connections":0, "recent":"", "old":"", "minimum":"", "maximum":""});

      }
    }
    else if(dataLength < this.dataLength) {
      for(let i=0; i<this.dataLength - dataLength; i++) {
        newData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), "date":"",
        "connections":0, "recent":"", "old":"", "minimum":"", "maximum":""});
      }

      for(let i=1; i<dataLength; i++) {
        newData.push({"xValue":(this.updateInterval)*(dataLength - i), "date":date[dataLength - i],
          "connections":parseInt(connections[dataLength - i], 10), "recent":recent[dataLength - i], "old":old[dataLength - i], 
          "minimum":minimum[dataLength - i], "maximum":maximum[dataLength - i]});
      }
      newData.push({"xValue":date[0], "connections":parseInt(connections[0], 10), "recent":recent[0], "old":old[0], 
          "minimum":minimum[0], "maximum":maximum[0]});

    }
    else {
      for(let i=0; i<this.dataLength - 1; i++) {
        newData.push({"xValue":(this.updateInterval)*(this.dataLength - i - 1), "date":date[this.dataLength - i - 1],
          "connections":parseInt(connections[this.dataLength - i -1], 10), "recent":recent[this.dataLength - i -1], "old":old[this.dataLength - i -1], 
          "minimum":minimum[this.dataLength - i -1], "maximum":maximum[this.dataLength - i -1]});
      }
      newData.push({"xValue":date[0], "date":date[0], "connections":parseInt(connections[0], 10), "recent":recent[0], "old":old[0], 
        "minimum":minimum[0], "maximum":maximum[0]});

    }

    console.log(newData);
    document.getElementById("connection").setData(newData); //newData로 차트 갱신
  }

  render() {
    const style = {
      width:'70vw',
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

        <Helmet>
          <script type="text/javascript" src="./chartSetting.js"></script>
        </Helmet>

        <div>
        <div style={style} id="connectionInfoChartHolder"/>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default ConnectionInfoChart;
