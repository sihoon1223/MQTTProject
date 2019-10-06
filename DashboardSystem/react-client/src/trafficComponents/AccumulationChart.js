import React, { Component, Fragment } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//accumulation chart component

class AccumulationChart extends Component {
  chartData = []; //초기 차트 데이터

  componentDidMount() {
    console.log("accumulate did mount");

    //차트 생성, 초기화
    makeChart.create("accumulate", "accumulationChartHolder", makeChart.accumulateLayoutStr, this.chartData);
  }

  componentWillUnmount() {
    console.log("accumulate unmount");
  }

  // "토픽개수;i번째토픽이름i번째토픽의accumulated; ... " 
  componentWillReceiveProps(nextProps) {
    if (this.props.chartData !== nextProps.chartData) {
      console.log("receive props");
      //this.props.chartData.map((message, i) => i===0 && this.messageArrived(message));
      this.messageArrived(this.props.chartData);
    }
  }

  messageArrived(data) {
    let dataArray = data.split(';');
    this.chartData =  [];

    for(let i=0; i<parseInt(dataArray[0], 10); i++) {
      this.chartData.push({"topic":dataArray[2*i + 1],"accumulated":parseInt(dataArray[2*i + 2], 10)});
    }
    document.getElementById("accumulate").setData(this.chartData);
  }

  render() {
    const style = {
      position:'absolute',
      width:'34.5vw',
      height: '42vh',
      top:'0vh',
      left:'36.5vw',
      background:'white',
      borderRadius: '25px',
      padding:'2%',
      margin:'1%',
      display:'inline-block',
      float:'left',
    };

    return (
      <Fragment>
        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>

        <div>
          <div style={style} id="accumulationChartHolder"/>
        </div>

        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default AccumulationChart;
