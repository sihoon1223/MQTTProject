import React, { Component, Fragment } from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import makeChart from '../js/MakeChart';

//topic pie chart component

class TopicPie extends Component {
  chartData = [];

  componentDidMount() {
    console.log("topic pie did mount");

    //차트 생성, 초기화
    makeChart.create("receivingPie", "receivingPieHolder", makeChart.receivingPieLayoutStr, this.chartData);
    makeChart.create("sendingPie", "sendingPieHolder", makeChart.sendingPieLayoutStr, this.chartData);
    makeChart.create("accumulationPie", "accumulationPieHolder", makeChart.accumulationPieLayoutStr, this.chartData);

  }

  componentWillUnmount() {
    console.log("topic pie unmount");
  }

  // "토픽개수;i번째토픽이름;i번째토픽의receiving;i번째토픽의sending; ... " 
  componentWillReceiveProps(nextProps) {
    if (this.props.receivingData !== nextProps.receivingData) {
      console.log(this.props.receivingData);
      this.messageArrived(this.props.receivingData, "receivingPie");  
    }
    else if(this.props.sendingData !== nextProps.sendingData) {
      this.messageArrived(this.props.sendingData, "sendingPie");  
    }
    else if(this.props.accumulationData !== nextProps.accumulationData) {
      this.messageArrived(this.props.accumulationData, "accumulationPie");  
    }
  }

  //"데이터개수;토픽이름;송or수or누적횟수;..."
  messageArrived(data, chartId) {
    let dataArray = data.split(';');

    let chartData =  [];
    for(let i=0; i<parseInt(dataArray[0], 10); i++) {
      chartData.push({"topic":dataArray[2*i + 1],"value":parseInt(dataArray[2*i + 2], 10)});
    }
    console.log(chartData);
    document.getElementById(chartId).setData(chartData);
  }

  render() {
    const divStyle = {
        position:'relative',
        width:'20vw',
        height: '87vh',
        top:'0vh',
        left:'71vw',
        background:'white',
        borderRadius: '25px',
        padding:'1%',
        margin:'1%',
      };

    const style1 = {
        width:'100%',
        height:'30%',
        padding:'5%',
        margin:'2%',
        marginBottom:'20px',
        display:'block',
        float:'left',
      };

      const style2 = {
        width:'100%',
        height:'30%',
        padding:'5%',
        margin:'2%',
        marginBottom:'20px',
        display:'block',
        float:'left',
      };

      const style3 = {
        width:'100%',
        height:'30%',
        padding:'5%',
        margin:'2%',
        marginBottom:'20px',
        display:'block',
        float:'left',
      };
      
    return (
      <Fragment>
        <Container fluid={true}>
        <Row className="justify-content-md-center">
        <Col>
        <div style={divStyle}>
            <div style={style1} id="receivingPieHolder"/>
            <div style={style2} id="sendingPieHolder"/>
            <div style={style3} id="accumulationPieHolder"/>
        </div>
        </Col>
        </Row>
        </Container>
      </Fragment>
    );
  }
}

export default TopicPie;
