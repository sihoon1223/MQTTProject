import React, { Component } from 'react';

export default class MakeChart extends Component {

  // create chart, init
  static create(chartId, divId, layout, data) {
    // rMateChart 를 생성합니다.
    window.rMateChartH5.create(chartId, divId, "", "100%", "100%");
    //rMateChartH5.calls 함수를 이용하여 차트의 준비가 끝나면 실행할 함수를 등록합니다.
    window.rMateChartH5.calls(chartId, {
        "setLayout" : layout,
        "setData" : data
    });
  }

  //server performance - cpu chart layout
  static cpuLayoutStr =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
        +'<Options>'
           +'<Caption text="Total CPU"/>'
            +'<SubCaption text="( % )" textAlign="right" />'
          +'<Legend />'
      +'</Options>'
      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
         +'<Area2DChart showDataTips="true">'
            +'<horizontalAxis>'
                +'<CategoryAxis id="hAxis" categoryField="xValue" padding="0.5"/>'
             +'</horizontalAxis>'
           +'<verticalAxis>'
              +'<LinearAxis formatter="{numfmt}"/>'
              +'<LinearAxis id="vAxis" maximum="100" minimum="0"/>'
          +'</verticalAxis>'
             +'<series>'
                +'<Area2DSeries yField="totalCpu" displayName="Cpu Utilization">'
                +'<areaStroke>'
                       +'<Stroke color="#FF4955" weight="3"/>'
                   +'</areaStroke>'
                  +'<areaFill>'
                     +'<SolidColor color="#FF4955" alpha="0.5"/>'
                 +'</areaFill>'
                 /* +'<showDataEffect>'  alpha="0.15"
                        +'<SeriesInterpolate/>'
                    +'</showDataEffect>'*/
               +'</Area2DSeries>'
             +'</series>'
             + '<horizontalAxisRenderers>'
               + '<Axis2DRenderer axis="{hAxis}" canDropLabels="true" showLine="true"/>'
             + '</horizontalAxisRenderers>'
       +'</Area2DChart>'
   +'</rMateChart>';

   //server performance - cpu chart layout
   static core1layoutStr =
   '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
        +'<Options>'
           +'<Caption text="CPU by core2"/>'
            +'<SubCaption text="( % )" textAlign="right" />'
          +'<Legend />'
      +'</Options>'
      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
         +'<Area2DChart showDataTips="true">'
            +'<horizontalAxis>'
                +'<CategoryAxis id="hAxis" categoryField="xValue" padding="0.5"/>'
             +'</horizontalAxis>'
           +'<verticalAxis>'
              +'<LinearAxis formatter="{numfmt}"/>'
              +'<LinearAxis id="vAxis" maximum="100" minimum="0"/>'
          +'</verticalAxis>'
             +'<series>'
                +'<Area2DSeries yField="core" displayName="Cpu Utilization">'
                +'<areaStroke>'
                       +'<Stroke color="#9669a3" weight="3"/>'
                   +'</areaStroke>'
                  +'<areaFill>'
                     +'<SolidColor color="#9669a3" alpha="0.5"/>'
                 +'</areaFill>'
                 /* +'<showDataEffect>'  alpha="0.15"
                        +'<SeriesInterpolate/>'
                    +'</showDataEffect>'*/
               +'</Area2DSeries>'
             +'</series>'
             + '<horizontalAxisRenderers>'
               + '<Axis2DRenderer axis="{hAxis}" canDropLabels="true" showLine="true"/>'
             + '</horizontalAxisRenderers>'
       +'</Area2DChart>'
   +'</rMateChart>';
   


static core2layoutStr =
   '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
         +'<Options>'
            +'<Caption text="CPU by core2"/>'
             +'<SubCaption text="( % )" textAlign="right" />'
           +'<Legend />'
       +'</Options>'
       +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
          +'<Combination2DChart showDataTips="true">'
             +'<horizontalAxis>'
                 +'<CategoryAxis categoryField="xValue" padding="0.5"/>'
              +'</horizontalAxis>'
            +'<verticalAxis>'
               +'<LinearAxis formatter="{numfmt}"/>'
               +'<LinearAxis id="vAxis" maximum="100" minimum="0"/>'

           +'</verticalAxis>'
              +'<series>'
                 +'<Area2DSeries yField="core" displayName="Cpu Utilization">'
                 +'<areaStroke>'
                        +'<Stroke color="#7f242a" weight="3"/>'
                    +'</areaStroke>'
                   +'<areaFill>'
                      +'<SolidColor color="#7f242a" alpha="0.5"/>'
                  +'</areaFill>'
                  /* +'<showDataEffect>'  alpha="0.15"
                         +'<SeriesInterpolate/>'
                     +'</showDataEffect>'*/
                +'</Area2DSeries>'


              +'</series>'
        +'</Combination2DChart>'
    +'</rMateChart>';




static core3layoutStr =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
          +'<Options>'
             +'<Caption text="CPU by core3"/>'
              +'<SubCaption text="( % )" textAlign="right" />'
            +'<Legend />'
        +'</Options>'
        +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
           +'<Combination2DChart showDataTips="true">'
              +'<horizontalAxis>'
                  +'<CategoryAxis categoryField="xValue" padding="0.5"/>'
               +'</horizontalAxis>'
             +'<verticalAxis>'
                +'<LinearAxis formatter="{numfmt}"/>'
                +'<LinearAxis id="vAxis" maximum="100" minimum="0"/>'

            +'</verticalAxis>'
               +'<series>'
                  +'<Area2DSeries yField="core" displayName="Cpu Utilization">'
                  +'<areaStroke>'
                         +'<Stroke color="#69965c" weight="3"/>'
                     +'</areaStroke>'
                    +'<areaFill>'
                       +'<SolidColor color="#69965c" alpha="0.5"/>'
                   +'</areaFill>'
                   /* +'<showDataEffect>'  alpha="0.15"
                          +'<SeriesInterpolate/>'
                      +'</showDataEffect>'*/
                 +'</Area2DSeries>'


               +'</series>'
         +'</Combination2DChart>'
     +'</rMateChart>';


static core4layoutStr =
                  '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
                        +'<Options>'
                           +'<Caption text="CPU by core4"/>'
                            +'<SubCaption text="( % )" textAlign="right" />'
                          +'<Legend />'
                      +'</Options>'
                      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
                         +'<Combination2DChart showDataTips="true">'
                            +'<horizontalAxis>'
                                +'<CategoryAxis categoryField="xValue" padding="0.5"/>'
                             +'</horizontalAxis>'
                           +'<verticalAxis>'
                              +'<LinearAxis formatter="{numfmt}"/>'
                              +'<LinearAxis id="vAxis" maximum="100" minimum="0"/>'

                          +'</verticalAxis>'
                             +'<series>'
                                +'<Area2DSeries yField="core" displayName="Cpu Utilization">'
                                +'<areaStroke>'
                                       +'<Stroke color="#5587a2" weight="3"/>'
                                   +'</areaStroke>'
                                  +'<areaFill>'
                                     +'<SolidColor color="#5587a2" alpha="0.5"/>'
                                 +'</areaFill>'
                                 /* +'<showDataEffect>'  alpha="0.15"
                                        +'<SeriesInterpolate/>'
                                    +'</showDataEffect>'*/
                               +'</Area2DSeries>'


                             +'</series>'
                       +'</Combination2DChart>'
                   +'</rMateChart>';


  //server performance - memory chart layout1
  static memoryLayoutStr1 =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
        +'<Options>'
           +'<Caption text="메모리"/>'
            +'<SubCaption text="Total Memory ( KB )" textAlign="right" />'
          +'<Legend />'
      +'</Options>'
      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
         +'<Area2DChart showDataTips="true">'
            +'<horizontalAxis>'
                +'<CategoryAxis id="hAxis" categoryField="xValue" padding="0.5"/>'
             +'</horizontalAxis>'
           +'<verticalAxis>'
              +'<LinearAxis formatter="{numfmt}"/>'
              +'<LinearAxis id="vAxis" ';

  //server performance - memory chart layout2
  static memoryLayoutStr2 =
    ' minimum="0"/>'
          +'</verticalAxis>'
             +'<series>'
                +'<Area2DSeries yField="memory" displayName="Used Memory">'
                +'<areaStroke>'
                       +'<Stroke color="#6FA6E7" weight="3"/>'
                   +'</areaStroke>'
                  +'<areaFill>'
                     +'<SolidColor color="#6FA6E7" alpha="0.5"/>'
                 +'</areaFill>'
               +'</Area2DSeries>'
             +'</series>'
             + '<horizontalAxisRenderers>'
               + '<Axis2DRenderer axis="{hAxis}" canDropLabels="true" showLine="true"/>'
             + '</horizontalAxisRenderers>'
       +'</Area2DChart>'
   +'</rMateChart>';

  //server performance - network chart layout
  static networkLayoutStr =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
        +'<Options>'
           +'<Caption text="네트워크 I/O"/>'
            +'<SubCaption text="" textAlign="right" />'
          +'<Legend />'
      +'</Options>'
      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
         +'<Area2DChart showDataTips="true">'
            +'<horizontalAxis>'
                +'<CategoryAxis id="hAxis" categoryField="xValue" padding="0.5"/>'
             +'</horizontalAxis>'
           +'<verticalAxis>'
              +'<LinearAxis formatter="{numfmt}"/>'
              +'<LinearAxis id="vAxis" interval="100"/>'
          +'</verticalAxis>'
             +'<series>'
                +'<Area2DSeries yField="in" displayName="Network In">'
                +'<areaStroke>'
                       +'<Stroke color="#B56EE8" weight="3"/>'
                   +'</areaStroke>'
                  +'<areaFill>'
                     +'<SolidColor color="#B56EE8" alpha="0.5"/>'
                 +'</areaFill>'
                 /* +'<showDataEffect>'  alpha="0.15"
                        +'<SeriesInterpolate/>'
                    +'</showDataEffect>'*/
               +'</Area2DSeries>'
               +'<Area2DSeries yField="out" displayName="Network Out">'
               +'<areaStroke>'
                      +'<Stroke color="#73E4E7" weight="3"/>'
                  +'</areaStroke>'
                 +'<areaFill>'
                    +'<SolidColor color="#73E4E7" alpha="0.5"/>'
                +'</areaFill>'
                /* +'<showDataEffect>'  alpha="0.15" FDE9AE
                       +'<SeriesInterpolate/>'
                   +'</showDataEffect>'*/
              +'</Area2DSeries>'
             +'</series>'
             + '<horizontalAxisRenderers>'
               + '<Axis2DRenderer axis="{hAxis}" canDropLabels="true" showLine="true"/>'
             + '</horizontalAxisRenderers>'
       +'</Area2DChart>'
   +'</rMateChart>';

  //message topic - topic chart layout
  static topicLayoutStr =
  '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
       +'<Options>'
          +'<Caption text="Topic별 송수신 횟수"/>'
		               +'<SubCaption text="( 개수 )" textAlign="right" />'

           +'<Legend />'
     +'</Options>'
     +'<NumberFormatter id="nft" precision="2"/>'
      +'<Line2DChart showDataTips="true" dataTipFormatter="{nft}">'
         +'<horizontalAxis>'
               +'<CategoryAxis id="hAxis" categoryField="topic"/>'
            +'</horizontalAxis>'
          +'<verticalAxis>'
             +'<LinearAxis id="vAxis" title="" minimum="0"/>'
           +'</verticalAxis>'
            +'<series>'
               +'<Line2DSeries yField="receiving" form="curve" displayName="Message Receiving Count" itemRenderer="CircleItemRenderer">'
                  
              +'</Line2DSeries>'
                +'<Line2DSeries yField="sending" form="curve" displayName="Message Sending Count" itemRenderer="CircleItemRenderer">'
                    
              +'</Line2DSeries>'
            +'</series>'
      +'</Line2DChart>'
 +'</rMateChart>';
  
    

  //message topic - accumulate chart layout
  static accumulateLayoutStr =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
          +'<Options>'
             +'<Caption text="Topic별 누적 메시지 크기"/>'
             +'<SubCaption text="( Byte )" textAlign="right" />'
              +'<Legend />'
        +'</Options>'
        +'<NumberFormatter id="nft" precision="2"/>'
         +'<Line2DChart showDataTips="true" dataTipFormatter="{nft}">'
            +'<horizontalAxis>'
                  +'<CategoryAxis id="hAxis" categoryField="topic"/>'
               +'</horizontalAxis>'
             +'<verticalAxis>'
                +'<LinearAxis id="vAxis" title="" minimum="0"/>'
              +'</verticalAxis>'
               +'<series>'
                  +'<Line2DSeries yField="accumulated" form="curve" displayName="Accumulated Message Size" itemRenderer="CircleItemRenderer">'
                  +'<lineStroke>'
                      +'<Stroke color="#8E5D9B" weight="3"/>'
                  +'</lineStroke>'   
                  /*+'<showDataEffect>'
                          +'<SeriesInterpolate duration="1000"/> '
                     +'</showDataEffect>'*/
                 +'</Line2DSeries>'
               +'</series>'
         +'</Line2DChart>'
    +'</rMateChart>';

   
  static receivingPieLayoutStr = 
  '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
  +'<Options>'
     +'<Caption text="수신 Topic TOP3"/>'
       //+'<Legend useVisibleCheck="true"/>'
  +'</Options>'
+'<Pie2DChart innerRadius="0.5" showDataTips="true" selectionMode="single">'
/*
Doughnut2D 차트 생성시에 필요한 Pie2DChart 정의합니다
showDataTips : 데이터에 마우스를 가져갔을 때 나오는 Tip을 보이기/안보이기 속성입니다.
innerRadius : PieChart 가운데에 빈 공간을 만듭니다. 유효값 0.1 ~ 0.9 0은 PieChart 1은 차트 사라짐
*/
     +'<series>'
          +'<Pie2DSeries nameField="topic" field="value" startAngle="20" renderDirection="clockwise" labelPosition="inside" color="#ffffff">'
             +'<fills>'
                   +'<SolidColor color="#20cbc2"/>'
                 +'<SolidColor color="#074d81"/>'
                 +'<SolidColor color="#40b2e6"/>'
             +'</fills>'
          /* Pie2DChart 정의 후 Pie2DSeries labelPosition="inside"정의합니다 */
              
         +'</Pie2DSeries>'
    +'</series>'
     +'<backgroundElements>'
          +'<CanvasElement>'
          +'<CanvasLabel text="Receiving" height="24" horizontalCenter="0" verticalCenter="-10" fontSize="12" color="#333333" backgroundAlpha="0"/>'
               +'<CanvasLabel text="Count" height="24" horizontalCenter="0" verticalCenter="10" fontSize="12" color="#666666" backgroundAlpha="0"/>'
        +'</CanvasElement>'
      +'</backgroundElements>'
 +'</Pie2DChart>'
+'</rMateChart>';
 

  static sendingPieLayoutStr = 
  '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
  +'<Options>'
     +'<Caption text="송신 Topic TOP3"/>'
       //+'<Legend useVisibleCheck="true"/>'
  +'</Options>'
+'<Pie2DChart innerRadius="0.5" showDataTips="true" selectionMode="single">'
/*
Doughnut2D 차트 생성시에 필요한 Pie2DChart 정의합니다
showDataTips : 데이터에 마우스를 가져갔을 때 나오는 Tip을 보이기/안보이기 속성입니다.
innerRadius : PieChart 가운데에 빈 공간을 만듭니다. 유효값 0.1 ~ 0.9 0은 PieChart 1은 차트 사라짐
*/
     +'<series>'
          +'<Pie2DSeries nameField="topic" field="value" startAngle="20" renderDirection="clockwise" labelPosition="inside" color="#ffffff">'
             +'<fills>'
             +'<SolidColor color="#FABC05"/>'
             +'<SolidColor color="#A78C3A"/>'
             +'<SolidColor color="#533201"/>'
             +'</fills>'
          /* Pie2DChart 정의 후 Pie2DSeries labelPosition="inside"정의합니다 */
              
         +'</Pie2DSeries>'
    +'</series>'
     +'<backgroundElements>'
          +'<CanvasElement>'
               +'<CanvasLabel text="Count" height="24" horizontalCenter="0" verticalCenter="10" fontSize="12" color="#666666" backgroundAlpha="0"/>'
               +'<CanvasLabel text="Sending" height="24" horizontalCenter="0" verticalCenter="-10" fontSize="12" color="#333333" backgroundAlpha="0"/>'
        +'</CanvasElement>'
      +'</backgroundElements>'
 +'</Pie2DChart>'
+'</rMateChart>';

  static accumulationPieLayoutStr = 
  '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
  +'<Options>'
     +'<Caption text="누적 Topic TOP3"/>'
       //+'<Legend useVisibleCheck="true"/>'
  +'</Options>'
+'<Pie2DChart innerRadius="0.5" showDataTips="true" selectionMode="single">'
/*
Doughnut2D 차트 생성시에 필요한 Pie2DChart 정의합니다
showDataTips : 데이터에 마우스를 가져갔을 때 나오는 Tip을 보이기/안보이기 속성입니다.
innerRadius : PieChart 가운데에 빈 공간을 만듭니다. 유효값 0.1 ~ 0.9 0은 PieChart 1은 차트 사라짐
*/
     +'<series>'
          +'<Pie2DSeries nameField="topic" field="value" startAngle="20" renderDirection="clockwise" labelPosition="inside" color="#ffffff">'
             +'<fills>'
                   +'<SolidColor color="#5E3E67"/>'
                 +'<SolidColor color="#B393BC"/>'
                 +'<SolidColor color="#CDAEC6"/>'
             +'</fills>'
          /* Pie2DChart 정의 후 Pie2DSeries labelPosition="inside"정의합니다 */
              
         +'</Pie2DSeries>'
    +'</series>'
     +'<backgroundElements>'
          +'<CanvasElement>'
               +'<CanvasLabel text="Size" height="24" horizontalCenter="0" verticalCenter="10" fontSize="12" color="#666666" backgroundAlpha="0"/>'
               +'<CanvasLabel text="Accumulated" height="24" horizontalCenter="0" verticalCenter="-10" fontSize="12" color="#333333" backgroundAlpha="0"/>'
        +'</CanvasElement>'
      +'</backgroundElements>'
 +'</Pie2DChart>'
+'</rMateChart>';


  //message client - connection info chart layout
  static connectionLayoutStr =
    '<rMateChart backgroundColor="#FFFFFF" borderStyle="none">'
        +'<Options>'
           +'<Caption text="Client Connection Info"/>'
            +'<SubCaption text="( 개수 )" textAlign="right" />'
          +'<Legend />'
      +'</Options>'
      +'<NumberFormatter id="numfmt" useThousandsSeparator="true"/>'
         +'<Area2DChart showDataTips="true"  dataTipJsFunction="dataTipFunc">'
            +'<horizontalAxis>'
                +'<CategoryAxis id="hAxis" categoryField="xValue" padding="0.5"/>'
             +'</horizontalAxis>'
           +'<verticalAxis>'
              +'<LinearAxis formatter="{numfmt}"/>'
              +'<LinearAxis id="vAxis" minimum="0" interval="10"/>'

          +'</verticalAxis>'
             +'<series>'
                +'<Area2DSeries yField="connections" displayName="current connections" itemRenderer="CircleItemRenderer">'
                +'<areaStroke>'
                       +'<Stroke color="#88B14B" weight="3"/>'
                   +'</areaStroke>'
                  +'<areaFill>'
                     +'<SolidColor color="#88B14B" alpha="0.5"/>'
                 +'</areaFill>'
                 /* +'<showDataEffect>'  alpha="0.15"
                        +'<SeriesInterpolate/>'
                    +'</showDataEffect>'*/
               +'</Area2DSeries>'
             +'</series>'
             + '<horizontalAxisRenderers>'
               + '<Axis2DRenderer axis="{hAxis}" canDropLabels="true" showLine="true"/>'
             + '</horizontalAxisRenderers>'
       +'</Area2DChart>'
   +'</rMateChart>';

}
