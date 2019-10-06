////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//		rMateChartH5 3.0 theme.js
//		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/***************************************************************************************
 *
 *		theme.js는 rMateChartH5 js 라이브러리 include 이후에 설정 되어야 합니다.
 *
 ***************************************************************************************/

(function(){

	// 각 기본 제공 테마의 기본 스타일 저장 변수
	var simple,
		cyber,
		modern,
		lovely,
		pastel;

	// target에 obj의 속성들을 넣어준다.
	function themeExtend(target, obj){
		for(var o in obj){
			if(!target[o])
				target[o] = obj[o];
		}
	}

	// Chart에 stroke, fill, areaFill등에 대한 값을 넣어줍니다.
	function intoProp(prop, theme, colors, alpha, weight, gradient){
		var i, n, o,
			color;
		
		if(alpha == null || alpha == undefined)
			alpha = 1;
		if(weight == null || weight == undefined)
			weight = 1;
		if(gradient == null || gradient == undefined)
			gradient = false;
		
		for(i = 0, n = colors.length ; i < n ; i += 1){
			color = colors[i % colors.length];
			if(!gradient){
				o = { color : color, weight : weight, alpha : alpha};
			}else{
				o = {};
				o.angle = 90;
				o.entries = [];
				o.entries.push({ color : color, ratio : 1, alpha : alpha});
			}
			
			if(!theme.series[i])
				theme.series[i] = {};

			theme.series[i][prop] = o;
		}
	}
	
	// rMateChartH5에서 제공하는 테마들의 저장 변수
	rMateChartH5.themes = {};

	// rMateChartH5에서 제공하는 테마들의 기본 스타일 저장 변수
	rMateChartH5.setupTheme = {};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//		Simple Theme
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// simple 기본 테마 스타일 저장
	rMateChartH5.setupTheme.simple = {
		defaultColors : [
			"#3BA0CC","#775F47","#A8C545","#DDCFAC","#777777","#9FB5CD","#2AA2A0","#37C07C","#3F596E","#7E92A5","#A48D70","#77B6D0","#CEE4ED","#555555",
			"#B195A6","#D4C4CE","#67425A","#B9DCDA","#3798B9","#89D5AF","#869726","#C1CC89","#836E2C","#C5BA8E","#816D5B","#DEE3E7","#84929F","#3B4B5B"
		],

		// Chart Common
		chartCommon : {
			gutterTop : 0, // 위쪽 축의 여백
			paddingRight : 0, // 차트 오른쪽의 여백
			paddingBottom : 0, // 차트 아래쪽의 여백
			dataTipBorderWidth : 2, // 차트 데이터 팁 div의 border 굵기
			showDataTipTargets : false
		},

		// Normal Chart
		normalChart : {
			paddingLeft : 0, // 차트 왼쪽의 여백
			gutterLeft : 0, // 왼쪽 축의 여백
			gutterRight : 0, // 오른쪽 축의 여백
			gutterBottom : 44, // 아래쪽 측의 여백
			verticalAxisRenderer : {
				showLine : false,
				showLabels : false,
				labelGap : 0
			},
			verticalAxisRenderers : [{ // 차트의 y축 렌더러 배열
				showLine : false, // 축 안보이기
				showLabels : false, // 축 라벨 안보이기
				labelGap : 0 // 축과 축 라벨의 여백
			},{
				showLine : false, // 축 안보이기
				showLabels : false, // 축 라벨 안보이기
				labelGap : 0 // 축과 축 라벨의 여백
			}],
			horizontalAxisRenderer : {
				labelGap : 14 // 축과 축 라벨의 여백
			}
		},	

		// Normal 3D Chart
		normal3DChart : {
			verticalAxisRenderer : {
				showLine : false,
				tickPlacement : "none"
			},
			verticalAxisRenderers : [{
				showLine : false, 
				tickPlacement : "none"
			}, {
				showLine : false, 
				tickPlacement : "none"
			}],
			horizontalAxisRenderer : {
				axisStroke : {color : "#dcccb2"}
			},
			horizontalAxisRenderers : [{
				axisStroke : {
					color : "#dcccb2"
				},
				showLine : true
			},{
				axisStroke : {
					color : "#dcccb2"
				},
				showLine : true
			}]
		},

		// History Chart
		historyChart : {
			navigator : {
				gutterBottom : 0	
			}
		},

		// Dual Chart
		dualChart : {
			subChart : { 
				gutterBottom : 0
			}
		},

		// Bar Chart
		barChart : {
			paddingLeft : 9, // 차트 왼쪽의 여백
			gutterBottom : 0, // 아래쪽 축의 여백
			horizontalAxisRenderers : [{ // 차트의 y축 렌더러 배열
				showLine : false, // 축 안보이기
				showLabels : false, // 축 라벨 안보이기
				labelGap : 0 // 축과 축 라벨의 여백
			}],
			verticalAxisRenderer : {
				labelGap : 10 // 축과 축 라벨의 여백
			}
		},

		// Bar 3D Chart
		bar3DChart : {
			paddingLeft : 9, // 차트 왼쪽의 여백
			gutterBottom : 0, // 아래쪽 축의 여백
			horizontalAxisRenderer : {
				showLine : false,
				showLabels : false,
				labelGap : 0,
				tickPlacement : "none"
			},
			horizontalAxisRenderers : [{ // 차트의 y축 렌더러 배열
				showLine : false, // 축 안보이기
				showLabels : false, // 축 라벨 안보이기
				labelGap : 0, // 축과 축 라벨의 여백
				tickPlacement : "none"
			}],
			verticalAxisRenderer : {
				axisStroke : {color : "#dcccb2"},
				labelGap : 10 // 축과 축 라벨의 여백
			},
			verticalAxisRenderers : [{
				axisStroke : {color : "#dcccb2"},
				labelGap : 10 // 축과 축 라벨의 여백
			},{
				axisStroke : {color : "#dcccb2"},
				labelGap : 10 // 축과 축 라벨의 여백
			}]
		},

		// Pie 3D Chart
		pieChart : {
			paddingLeft : 100,
			paddingRight : 100,
			paddingTop : 50,
			paddingBottom : 50
		},

		// Line 2D Chart
		lineChart : {
			series : [
			]
		},

		// Area 2D Chart
		areaChart : {
			series : [
			]
		},

		realTimeChart : {
			series : [
			]
		},

		// Radar Chart
		radarChart : {
			paddingBottom : 20
		},

		// Wing Bar 2D Chart
		wingBarChart : {},

		// Wing Column 2D Chart
		wingColumnChart : {
			paddingLeft : 0,
			paddingTop : 0,
			paddingRight : 0,
			paddingBottom : 0,
			gutterLeft : 0,
			gutterRight :0,
			dataTipBorderWidth : 2,
			showDataTipTargets : false,
			verticalAxisRenderers : [{
				showLine : false,
				showLabels : false,
				lagelGap : 0
			},{
				showLine : false,
				showLabels : false,
				lagelGap : 0
			}],
			horizontalAxisRenderer : {
				labelGap : 14
			}
		},

		// Line 2D Series
		lineSeries : {
			fill : "#ffffff",
			itemRenderer : "CircleItemRenderer"
		},

		// Area 2D Series
		areaSeries : {
			form : "segment"
		},

		// Column Bar 2D Series
		columnBarSeries : {
			itemRenderer : "BoxItemRenderer"
		},

		// Combination
		combinationChart : {
			series : [
			]
		},

		targetGoalSeries : {
			stroke : {"color":"#AACDE4", "weight":1},
			fill : {"color":"#AACDE4"}
		},

		// Circular Gauge Common
		circularCommon : {
			frameStroke : {"color":"#3B4B5B", "weight":2},
			frameFill : {"color":"#3BA0CC"},
			needleStroke : {"color":"#77B6D0"},
			needleFill : {"color":"#77B6D0", "alpha":1},
			needleCoverFill : {"color":"#3F596E"},
			needleCoverStroke : {"color":"#3F596E"},
			minorTickFill : {"color":"#777777"},
			minorTickStroke : {"color":"#3B4B5B"},
			tickFill : {"color":"#84929F"},
			tickStroke : {"color":"#3B4B5B"},
			tickGap : 4,
			labelGap : 8,
			color : "#454545",
			tickLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				fontSize : 14,
				color : "#DEE3E7"
			},
			valueLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				color : "#454545",
				fontSize : 14,
				borderStyle : "none",
				borderThickness : 2,
				borderColor : "#3B4B5B"
			},
			hideTickLabel : "none" // 틱 라벨을 숨기지 않게 설정합니다.
		},

		// Circular Gauge 
		circular : {
			minimumAngle : 40,
			maximumAngle : 320,
			valueXOffset : 0,
			valueYOffset : 80
		},

		// Half Circular Gauge
		halfCircular : {
			minimumAngle : 190,
			maximumAngle : 350,
			valueYOffset : -50
		},

		// VCylinder, HCylinder Gauge
		cylinderGauge : {
			cylinderColor : ["#3BA0CC", "#FFFFFF", "#3BA0CC"],
			valueLabelStyleName : {
				fontSize : 12,
				color : "#454545",
				fontWeight : "bold"
			},
			tickLabelStyleName : {
				fontSize : 12
			},
			tickColor : "#000000"
		},

		// VLinear, HLinear Gauge
		linearGauge : {
			linearBorderColor : "#77B6D0",
			linearColor : ["#77B6D0", "#77B6D0", "#77B6D0"],
			linearBgBorderColor : "#7E92A5",
			linearBgColor : ["#7E92A5", "#7E92A5", "#7E92A5"],
			targetMarkBorderColor : "#CEE4ED",
			targetMarkColor : ["#CEE4ED", "#CEE4ED", "#CEE4ED"],
			valueLabelStyleName : {
				fontSize : 13,
				color : "#ffffff"
			},
			tickLabelStyleName : {
				fontSize : 12
			}
		}
	};

	// simple의 기본 테마를 저장합니다.
	simple = rMateChartH5.setupTheme.simple;

	// Normal Chart에 ChartCommon 테마를 저장합니다.
	themeExtend(simple.normalChart, simple.chartCommon);

	// Normal 3D Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.normal3DChart, simple.normalChart);

	// Bar Chart에 themeChartCommon 테마를 저장합니다.
	themeExtend(simple.barChart, simple.chartCommon);

	// Bar 3D Chart에 themeChartCommon 테마를 저장합니다.
	themeExtend(simple.bar3DChart, simple.chartCommon);

	// Line Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.lineChart, simple.normalChart);

	// Area Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.areaChart, simple.normalChart);

	// Pie Chart에 ChartCommon 테마를 저장합니다.
	themeExtend(simple.pieChart, simple.chartCommon);

	// Radar Chart에 ChartCommon 테마를 저장합니다.
	themeExtend(simple.radarChart, simple.chartCommon);

	// History Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.historyChart, simple.normalChart);

	// Dual Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.dualChart, simple.normalChart);
	
	// Wing Bar Chart에 barChart 테마를 저장합니다.
	themeExtend(simple.wingBarChart, simple.barChart);

	// Circular Gauge에 CircularCommon 테마를 저장합니다.
	themeExtend(simple.circular, simple.circularCommon);

	// Half Circular Gauge에 CircuarCommon 테마를 저장합니다.
	themeExtend(simple.halfCircular, simple.circularCommon);

	// Combiantion 2D Chart에 NormalChart 테마를 저장합니다.
	themeExtend(simple.combinationChart, simple.normalChart);

	// Line Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", simple.lineChart, simple.defaultColors, 1, 2);

	// Area Chart의 series에 areaFill을 설정합니다.
	intoProp("areaFill", simple.areaChart, simple.defaultColors, 0.7, 2, true);

	// Combination 2D Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", simple.combinationChart, simple.defaultColors, 1, 2);
	
	// RealTimeChart의 series에 fill을 설정합니다.
	intoProp("stroke", simple.realTimeChart, simple.defaultColors, 1, 2);

	//--------------------------------------------------------------------------------
	//
	//	실제적인 simple theme
	//
	//--------------------------------------------------------------------------------
	rMateChartH5.themes.simple = {
		// default color
		defaultColors : simple.defaultColors,
		
		// rMateChart
		rMateChart : {
			background : "linear-gradient(#f5f5f5, #f9f9f9)",
			border : "solid 1px #e6e6e6",
			borderRadius : 0,
			paddingLeft : 0,
			paddingRight : 0,
			paddingBottom : 0
		},

		// NormalChart
		Column2DChart : simple.normalChart,
		Pie2DChart : simple.normalChart,
		Plot2DChart : simple.normalChart,
		RealTimeChart : simple.normalChart,
		Matrix2DChart : simple.normalChart,
		ImageChart : simple.normalChart,
		Candlestick2DChart : simple.normalChart,
		CandleLine2DChart : simple.normalChart,
		CandleArea2DChart : simple.normalChart,
		
		// Normal3DChart
		Column3DChart : simple.normal3DChart,
		Bubble3DChart : simple.normal3DChart,

		// LineChart
		Line2DChart : simple.lineChart,

		// AreaChart
		Area2DChart : simple.areaChart,

		// BarChart
		Bar2DChart : simple.barChart,

		// Bar3DChart
		Bar3DChart : simple.bar3DChart,

		// PieChart
		Pie3DChart : simple.pieChart,

		// RadarChart
		RadarChart : simple.radarChart,

		// HistoryChart
		HistoryChart : simple.historyChart,

		// DualChart
		DualChart : simple.dualChart,

		// CombinationChart
		Combination2DChart : simple.combinationChart,
		//Combination3DChart : simple.combinationChart,
		Combination3DChart : simple.normal3DChart,

		// WingColumnChart
		Column2DWingChart : simple.wingColumnChart,

		// WingBarChart
		Bar2DWingChart : simple.wingBarChart,

		VTarget3DGoalSeries : simple.targetGoalSeries,
		HTarget3DGoalSeries : simple.targetGoalSeries,

		RealTimeChart : simple.realTimeChart,

		// Circular
		CircularGauge : simple.circular,

		// HalfCircular
		HalfCircularGauge : simple.halfCircular,
		
		// CylinderGauge
		VCylinderGauge : simple.cylinderGauge,
		HCylinderGauge : simple.cylinderGauge,
		
		// LinearGauge
		VLinearGauge : simple.linearGauge,
		HLinearGauge : simple.linearGauge,
		
		// Series
		Line2DSeries : simple.lineSeries,
		Column2DSeries : simple.columnBarSeries,
		Bar2DSeries :simple.columnBarSeries,
		Area2DSeries : simple.areaSeries,
		
		// Elements
		GridLines : {
			direction : "none"
		}
	};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//		Cyber Theme
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// cyber 테마 기본 스타일 저장
	rMateChartH5.setupTheme.cyber = {
		defaultColors : [
			"#F5F5F5","#96D3CB","#F86E2D","#FEF6C5","#C3B689","#665A44","#BF9D77","#74C092","#E1F2BB","#FFC961","#FB937B","#EB494A","#999999","#CDCDCD",
			"#73ADB1","#BED5D9","#524E4F","#A8D37A","#CCE5A2","#FFFFFF","#76C6BE","#C1E2DE","#B06876","#E2C1C8","#7A6E67","#9E948D","#EA7A29","#EFC18A"
		],

		// Chart Common
		chartCommon : {
			dataTipBackgroundColorOnSeries : true,
			showDataTipTargets : false,
			dataTipBorderColor : "#ffffff"
		},

		// Normal Chart
		normalChart : {
			gutterBottom : 47
		},
		
		// Normal 3D Chart
		normal3DChart : {
			gutterBottom : 47,
			verticalAxisRenderer : {
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},horizontalAxisRenderer : {
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},
			horizontalAxisRenderers : [{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			}],
			verticalAxisRenderers : [{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			}]
		},

		// Line 2D Chart
		lineChart : {
			series : [
			]
		},
		
		// Line 2D Series
		lineSeries : {
			color : "#E40050", // 수치 라벨 색상
			itemRenderer : "" // 아이템 렌더러
		},

		// Area 2D Chart
		areaChart : {
			series :[
			]
		},

		// Area 2D Series
		areaSeries : {
			//labelPosition : "none",
			color : "#E40050",
			form : "segment",
			areaFill : {color:"#8a8a8a", alpha:0.5}
		},

		// Bar 2D Chart
		barChart : {
				// Elements
			backgroundElements : [{
				direction : "vertical",
				verticalStroke : {"color":"#333231"}
			}]
		},

		// Bar 3D Chart
		bar3DChart : {
			horizontalAxisRenderer : {
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},
			horizontalAxisRenderers : [{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			},{
				axisStroke : {color:"#454545"},
				tickStroke : {color:"#4f4f4f"},
				tickPlacement : "cross"
			}]
		},

		// Column Bar 2D Series
		columnBarSeries : {
			color : "#E40050",
			itemRenderer : "BoxItemRenderer"
		},

		// Column Bar 3D Series
		columnBar3DSeries : {
			color : "#E40050"
		},

		// Pie Series
		pieSeries : {
			color : "#E40050"
		},
		
		// V,H Target Goal Series
		targetGoalSeries : {
			stroke : {"color":"#007492", "weight":1},
			fill : {"color":"#007492"}
		},
		
		// Radar Chart
		radarChart : {
			color : "#ffffff"
		},

		// Circular Gauge Common
		circularCommon : {
			frameStroke : {"color":"#74C092", "weight":5}, // 게이지의 테두리 선을 설정합니다.
			frameFill : {"color":"#E1F2BB"}, // 게이지의 테두리 안쪽 영역의 채우기 색상을 설정합니다.
			needleStroke : {"color":"#96D3CB"}, // 게이지 바늘의 테두리 선을 설정합니다.
			needleFill : {"color":"#96D3CB", "alpha":1}, // 게이지 바늘의 안쪽 영역의 채우기 색상을 설정합니다.
			needleCoverRadius : 8, // 게이지 덮개의 크기를 설정합니다.
			needleCoverFill : {"color":"#74C092"}, // 게이지 바늘의 덮개 채우기 색상을 설정합니다.
			needleCoverStroke : {"color":"#74C092", "weight":3}, // 게이지 바늘의 덮개 테두리 선을 설정합니다.
			minorTickFill : {"color":"#777777"}, // 게이지의 마이너 틱의 채우기 색상을 설정합니다.
			minorTickStroke : {"color":"#3B4B5B"}, // 게이지 마이너 틱의 테두리 선을 설정합니다.
			tickFill : {"color":"#84929F"}, // 게이지 틱의 채우기 색상을 설정합니다.
			tickStroke : {"color":"#3B4B5B"}, // 게이지 틱의 테두리 선을 설정합니다.
			tickGap : 4, // 테두리와 틱의 여백을 설정합니다.
			labelGap : 8, // 틱과 틱 라벨의 여백을 설정합니다.
			needleBackLengthRatio : 0.4, // 게이지 바늘의 덮개를 기준으로 뒷부분의 길이를 설정합니다.
			tickLabelStyleName : { // 틱 라벨에 대해 폰트를 설정합니다.
				fontFamily : "arial,맑은 고딕",
				fontSize : 14,
				color : "#454545"
			},
			valueLabelStyleName : { // 밸류 라벨에 대해 폰트를 설정합니다.
				fontFamily : "arial,맑은 고딕",
				color : "#454545",
				fontSize : 14,
				borderStyle : "none",
				borderThickness : 2,
				borderColor : "#3B4B5B"
			},
			valueXOffset : 0, // 밸류 라벨의 x위치를 설정합니다.
			hideTickLabel : "none" // 틱 라벨을 숨기지 않게 설정합니다.
		},

		// Circular Gauge
		circular : {
			minimumAngle : 30,
			maximumAngle : 330,
			valueYOffset : 60
		},

		// Half Circular Gauge
		halfCircular : {
			minimumAngle : 190,
			maximumAngle : 350,
			valueYOffset : -50
		},

		// VCylinder, HCylinder Gauge
		cylinderGauge : {
			cylinderColor : ["#EB494A", "#FB937B", "#EB494A"],
			cylinderAlpha : [1, 1, 1],
			tickColor : "#ffffff",
			valueLabelStyleName : {
				fontSize : 14
			},
			tickLabelStyleName : {
				fontSize : 14
			}
		},

		// VLinear, HLinear Gauge
		linearGauge : {
			linearBorderColor : "#F86E2D",
			linearColor : ["#F86E2D", "#F86E2D", "#F86E2D"],
			linearBgBorderColor : "#96D3CB",
			linearBgColor : ["#96D3CB", "#96D3CB", "#96D3CB"],
			linearBgAlpha : [1, 1, 1],
			targetMarkBorderColor : "#F5F5F5",
			targetMarkColor : ["#F5F5F5", "#F5F5F5", "#F5F5F5"],
			valueLabelStyleName : {
				fontSize : 13,
				color : "#ffffff"
			},
			tickColor : "#ffffff",
			tickLabelStyleName : {
				fontSize : 12
			}
		},
		
		// CandleLine2DChart, CandleArea2DChart
		candleLine2DChart : {
			dataTipStyleName : {
				color : "#ffffff"
			}
		}
	};
	
	// cyber의 기본 테마를 저장합니다.
	cyber = rMateChartH5.setupTheme.cyber;

	// Normal Chart에 Chart Common 테마를 저장합니다.
	themeExtend(cyber.normalChart, cyber.chartCommon);

	// Normal 3D Chart에 Chart Common 테마를 저장합니다.
	themeExtend(cyber.normal3DChart, cyber.chartCommon);

	// Line Chart에 ChartCommon 테마를 저장합니다.
	themeExtend(cyber.lineChart, cyber.normalChart);

	// Bar Chart에 Chart Common 테마를 저장합니다.
	themeExtend(cyber.barChart, cyber.chartCommon);

	// Radar Chart에 chart common 테마를 저장합니다.
	themeExtend(cyber.radarChart, cyber.chartCommon);

	// Bar 3D Chart에 Bar Chart 테마를 저장합니다.
	themeExtend(cyber.bar3DChart, cyber.barChart);

	// Circular Gauge에 CircularCommon 테마를 저장합니다.
	themeExtend(cyber.circular, cyber.circularCommon);

	// Half Circular Gauge에 CircularCommon 테마를 저장합니다.
	themeExtend(cyber.halfCircular, cyber.circularCommon);

	// CandleLine2DChart에 chartCommon테마를 저장합니다.
	themeExtend(cyber.candleLine2DChart, cyber.chartCommon);

	// Line Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", cyber.lineChart, cyber.defaultColors, 1, 2);

	//--------------------------------------------------------------------------------
	//
	//	실제적인 cyber Theme
	//
	//--------------------------------------------------------------------------------
	rMateChartH5.themes.cyber = {
		// default color
		defaultColors : cyber.defaultColors,

		// rMateChart
		rMateChart : {
			background : "linear-gradient(#454545, #131212)"
		},

		// Caption
		Caption : {
			color : "#ffffff"
		},

		//SubCaption
		SubCaption :{
			color: "#ffffff"
		},

		//Label
		Label : {
			color : "#ffffff"
		},

		Legend : {
			backgroundColor : "#000000",
			color : "#ffffff"
		},
		
		// Axis2DRenderer - x axis, y axis
		Axis2DRenderer : {
			color : "#bdbdbd",
			showLine : false
		},

		// Axis2DWingRenderer - Wing Chart x axis, y axis
		Axis2DWingRenderer : {
			color : "#bdbdbd",
			showLine : false
		},

		// BrokenAxis2DRenderer - x axis, y axis
		BrokenAxis2DRenderer : {
			color : "#bdbdbd",
			showLine : false
		},
		
		BarSet : {
			color : "#E40050"
		},

		Bar2DWingSeries  : {
			color : "#E40050"
		},

		ColumnSet : {
			color : "#E40050"
		},

		Column2DWingSeries  : {
			color : "#E40050"
		},

		VTarget2DGoalSeries : {
			color : "#E40050"
		},

		VTarget2DResultSeries : {
			color : "#454545"
		},

		// chartCommon
		Column2DChart : cyber.chartCommon,
		Pie2DChart : cyber.chartCommon,
		Pie3DChart : cyber.chartCommon,
		Plot2DChart : cyber.chartCommon,
		Combination2DChart : cyber.chartCommon,
		HistoryChart : cyber.chartCommon,
		RealTimeChart : cyber.chartCommon,
		Matrix2DChart : cyber.chartCommon,
		ImageChart : cyber.chartCommon,
		Column2DWingChart : cyber.chartCommon,
		Bar2DWingChart : cyber.chartCommon,
		Candlestick2DChart : cyber.chartCommon,

		// normal3DChart
		Column3DChart : cyber.normal3DChart,
		Bubble3DChart : cyber.normal3DChart,
		Combination3DChart : cyber.normal3DChart,

		// lineChart
		Line2DChart : cyber.lineChart,

		// areaChart
		Area2DChart : cyber.areaChart,

		// barChart
		Bar2DChart : cyber.barChart,

		// bar3DChart
		Bar3DChart : cyber.bar3DChart,
		
		// radarChart
		RadarChart : cyber.radarChart,

		// circular
		CircularGauge : cyber.circular,

		// halfCircular
		HalfCircularGauge : cyber.halfCircular,

		// cylinderGauge
		VCylinderGauge : cyber.cylinderGauge,
		HCylinderGauge : cyber.cylinderGauge,

		// linearGauge
		VLinearGauge : cyber.linearGauge,
		HLinearGauge : cyber.linearGauge,
		
		// lineSeries
		Line2DSeries : cyber.lineSeries,

		// areaSeries
		Area2DSeries : cyber.areaSeries,
		
		// columnBarSeries
		Column2DSeries : cyber.columnBarSeries,
		Bar2DSeries : cyber.columnBarSeries,

		// pieSeries
		Pie2DSeries : cyber.pieSeries,
		Pie3DSeries : cyber.pieSeries,

		// columnBar3DSeries
		Column3DSeries : cyber.columnBar3DSeries,
		Bar3DSeries : cyber.columnBar3DSeries,

		// Target Goal Series
		VTarget3DGoalSeries : cyber.targetGoalSeries,
		HTarget3DGoalSeries : cyber.targetGoalSeries,

		// Matrix
		Matrix2DSeries : {
			color : "#e40050"
		},
		
		// GridLines
		GridLines : {
			direction : "horizontal",
			horizontalStroke : {"color":"#4c4c4c"}
		},

		// Radar Grid Lines
		RadarGridLines : {
			radarFill : {color : "#454545", alpha : 0.5},
			radarAlternateFill : {color : "#999999", alpha : 0.5}
		},

		// candleLine2DChart
		CandleLine2DChart : cyber.candleLine2DChart,
		CandleArea2DChart : cyber.candleLine2DChart
	};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//		Modern Theme
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// cyber 테마 기본 스타일 저장
	rMateChartH5.setupTheme.modern = {
		defaultColors : [
			"#414C75","#327AC3","#6CABE7","#93BFEB","#BED9ED","#00667C","#007A97","#0092BA","#00AECE","#00C4DC","#6FD2E4","#A2DBEB","#11568C","#4E70BD",
			"#678AD8","#7E9FE9","#A8C0F7","#CBDBFE","#01919E","#2CBFB9","#26CCC5","#1EDDD5","#13F1E8","#4DFDF6","#94FFFB","#00C8A4","#00EAC1","#5CFFE2"
		],

		// Chart Common
		chartCommon : {
			dataTipBorderColor : "#e8e8e8",
			showDataTipTargets : false
		},

		// Normal Chart
		normalChart : {
			gutterLeft : 7,
			gutterRight : 7,
			verticalAxisRenderer : {
				showLine : false,
				showLabels : false,
				labelGap : 0,
				tickPlacement : "none"
			},
			verticalAxisRenderers : [{
				showLine : false,
				showLabels : false,
				labelGap : 0,
				tickPlacement : "none"
			},{
				showLine : false,
				showLabels : false,
				labelGap : 0,
				tickPlacement : "none"
			}]
		},

		// Normal 3D Chart
		normal3DChart : {
			gutterLeft : 44,
			horizontalAxisRenderer : {
				axisStroke : {color:"#d1dbda"},
				showLine : true
			},
			horizontalAxisRenderers : [{
				axisStroke : {color:"#d1dbda"},
				showLine : true
			},{
				axisStroke : {color:"#d1dbda"},
				showLine : true
			}],
			verticalAxisRenderer : {
				axisStroke : {color:"#d1dbda"},
				showLine : true
				//axisStroke : {color:"#ffffff"},
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			},verticalAxisRenderers : [{
				axisStroke : {color:"#d1dbda"},
				showLine : true
				//axisStroke : {color:"#ffffff"},
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			},{
				axisStroke : {color:"#d1dbda"},
				showLine : true
				//axisStroke : {color:"#ffffff"},
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			}]
		},

		// Line 2D Chart
		lineChart : {
			series : [
			]
		},

		// Line 2D Series
		lineSeries : {
			fill : "#ffffff",
			itemRenderer : "CircleItemRenderer"
		},

		// Area 2D Chart
		areaChart : {
			series : [
			]
		},

		// Area 2D Series
		areaSeries : {
			form : "segment"
		},

		// Bar 2D Chart
		barChart : {
			gutterBottom : 5,
			horizontalAxisRenderer : {
				showLine : false,
				showLabels : false,
				labelGap : 0
			},
			verticalAxisRenderer : {
				showLine : false,
				showLabels : true
			},
			verticalAxisRenderers : [{
				showLine : false,
				showLabels : true
			},{
				showLine : false,
				showLabels : true
			}]
		},

		// Bar 3D Chart
		bar3DChart : {
			gutterBottom : 30,
			horizontalAxisRenderer : {
				showLine : true,
				showLabels : true,
				axisStroke : {color:"#d1dbda"}
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			},
			horizontalAxisRenderers : [{
				showLine : true,
				axisStroke : {color:"#d1dbda"},
				showLabels : true
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			},{
				showLine : true,
				axisStroke : {color:"#d1dbda"},
				showLabels : true
				//tickStroke : {color:"#ecf0ef"},
				//tickPlacement : "cross"
			}],
			verticalAxisRenderer : {
				axisStroke : {color:"#d1dbda"},
				showLine : true
			},verticalAxisRenderers : [{
				axisStroke : {color:"#d1dbda"},
				showLine : true
			},{
				axisStroke : {color:"#d1dbda"},
				showLine : true
			}]
		},

		// Combinaion Chart
		combinationChart : {
			series : [
			]
		},

		// Combinaion Chart
		combination3DChart : {
			series : [
			]
		},

		// History Chart
		historyChart : {
			navigator : {
				gutterLeft : 0
			}
		},
		
		// Real Time Chart
		realTimeChart : {
			series : []
		},

		// Wing Bar 2D Chart
		wingBarChart : {
		},

		// Wing Column 2D Chart
		wingColumnChart : {
			verticalAxisRenderers : [{
				showLabels : false,
				labelGap : 0
			},{
				showLabels : false,
				labelGap : 0
			}]
		},

		// V, H Target 3D Goal Series
		targetGoalSeries : {
			stroke : {color : "#AACDE4", weight:1},
			fill : {color : "#AACDE4"}
		},

		// Column Bar 2D Series
		columnBarSeries : {
			halfWidthOffset : 0.5,
			itemRenderer : "BoxItemRenderer"
		},

		// Dual Chart
		columnBarSeries : {
		},

		// Circular Gauge Common
		circularCommon : {
			frameStroke : {"color":"#11568C", "weight":10},
			frameFill : {"color":"#4E70BD"},
			needleStroke : {"color":"#6CABE7"},
			needleFill : {"color":"#6CABE7", "alpha":1},
			needleCoverFill : {"color":"#414C75"},
			needleCoverStroke : {"color":"#414C75", "weight":3},
			minorTickFill : {"color":"#6CABE7"},
			minorTickStroke : {"color":"#6CABE7"},
			tickFill : {"color":"#327AC3"},
			tickStroke : {"color":"#11568C"},
			tickGap : 4,
			labelGap : 8,
			needleBackLengthRatio : 0,
			needleCoverRadius : 10,
			color : "#454545",
			tickLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				fontSize : 14,
				color : "#ffffff"
			},
			valueLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				color : "#454545",
				fontSize : 14,
				borderStyle : "none",
				borderThickness : 2,
				borderColor : "#3B4B5B"
			},
			valueXOffset : 0,
			needleThickness : 15,
			hideTickLabel : "none" // 틱 라벨을 숨기지 않게 설정합니다.
		},

		// Circular Gauge
		circular : {
			minimumAngle : 20,
			maximumAngle : 340,
			valueYOffset : 60
		},

		// Half circular Gauge
		halfCircular : {
			minimumAngle : 190,
			maximumAngle : 350,
			valueYOffset : -50
		},


		// VCylinder, HCylinder Gauge
		cylinderGauge : {
			cylinderColor : ["#6CABE7", "#93BFEB", "#6CABE7"],
			targetMarkColor : ["#BED9ED", "#BED9ED", "#BED9ED"]
		},

		// VLinear, HLinear Gauge
		linearGauge : {
			linearBorderColor : "#7E9FE9",
			linearColor : ["#7E9FE9", "#7E9FE9", "#7E9FE9"],
			linearRatio : ["0", "125", "255"],
			linearAlpha : ["0.5", "0.8", "1"],
			linearBgBorderColor : "#414C75",
			linearBgColor : ["#414C75", "#414C75", "#414C75"],
			targetMarkBorderColor : "#327AC3",
			targetMarkColor : ["#327AC3", "#327AC3", "#327AC3"],
			targetMarkRatio : [0, 125, 255],
			targetMarkAlpha : [0.5, 0.8, 1],
			valueLabelStyleName : {
				fontSize : 13,
				color : "#ffffff",
				fontWeight : "bold"
			},
			tickLabelStyleName : {
				fontSize : 12
			}
		}
	};

	// modern 테마의 기본 스타일을 저장합니다.
	modern = rMateChartH5.setupTheme.modern;

	// Normal Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(modern.normalChart, modern.chartCommon);

	// Normal 3D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(modern.normal3DChart, modern.normalChart);

	// Line 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(modern.lineChart, modern.normalChart);

	// Area 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(modern.areaChart, modern.normalChart);

	// Bar 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(modern.barChart, modern.chartCommon);

	// Bar 3D Chart에 barChart의 테마를 저장합니다.
	themeExtend(modern.bar3DChart, modern.barChart);

	// Combination Chart에 normalChart의 테마를 저장합니다.
	themeExtend(modern.combinationChart, modern.normalChart);

	// Combination 3D Chart에 normalChart의 테마를 저장합니다.
	themeExtend(modern.combination3DChart, modern.normalChart);

	// History Chart에 normalChart의 테마를 저장합니다.
	themeExtend(modern.historyChart, modern.normalChart);

	// Wing Column 2D Chart에 normalChart의 테마를 저장합니다.
	themeExtend(modern.wingColumnChart, modern.normalChart);

	// circular Gauge에 Cicular Gauge Common의 테마를 저장합니다.
	themeExtend(modern.circular, modern.circularCommon);

	// Half circular Gauge에 Ciruclar Gauge Common의 테마를 저장합니다.
	themeExtend(modern.halfCircular, modern.circularCommon);

	// Line Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", modern.lineChart, modern.defaultColors, 1, 2);

	// Line Chart의 series에 declineStroke를 설정합니다.
	intoProp("declineStroke", modern.lineChart, ["#00c0c7"], 1, 2);

	// Area Chart의 series에 areaFill을 설정합니다.
	intoProp("areaFill", modern.areaChart, modern.defaultColors, 0.5, 2, true);

	// Combination Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", modern.combinationChart, modern.defaultColors, 1, 2);

	// Combination Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", modern.combination3DChart, modern.defaultColors, 1, 2);

	// RealTime Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", modern.realTimeChart, modern.defaultColors, 1, 2);

	//--------------------------------------------------------------------------------
	//
	//	실제적인 modern Theme
	//
	//--------------------------------------------------------------------------------
	rMateChartH5.themes.modern = {
			// default color
			defaultColors : modern.defaultColors,
			
			// rMateChart
			rMateChart : {
				background : "linear-gradient(#eaeeee, #ffffff)",
				border : "solid 1px #e2e7e7",
				borderRadius : 0
			},
			
			// Axis2DRenderer - x axis, y axis
			Axis2DRenderer : {
				color : "#575c6e",
				showLine : false,
				fontWeight : "bold"
			},

			// Axis3DRenderer - x axis, y axis
			Axis3DRenderer : {
				color : "#575c6e",
				showLine : false,
				fontWeight : "bold",
				tickPlacement : "none"
			},

			// BrokenAxis2DRenderer - x axis, y axis
			BrokenAxis2DRenderer : {
				color : "#575c6e",
				showLine : false,
				fontWeight : "bold"
			},
			
			// normalChart
			Column2DChart : modern.normalChart,
			Pie2DChart : modern.normalChart,
			Pie3DChart : modern.normalChart,
			Plot2DChart : modern.normalChart,
			RadarChart : modern.normalChart,
			RealTimeChart : modern.normalChart,
			Matrix2DChart : modern.normalChart,
			ImageChart : modern.normalChart,
			Candlestick2DChart : modern.normalChart,
			CandleLine2DChart : modern.normalChart,
			CandleArea2DChart : modern.normalChart,
			
			// normal3DChart
			Column3DChart : modern.normal3DChart,
			Bubble3DChart : modern.normal3DChart,

			// lineChart
			Line2DChart : modern.lineChart,

			// areaChart
			Area2DChart : modern.areaChart,

			// barChart
			Bar2DChart : modern.barChart,

			// bar3DChart
			Bar3DChart : modern.bar3DChart,

			// combinationChart
			Combination2DChart : modern.combinationChart,

			// combination3DChart
			//Combination3DChart : modern.combination3DChart,
			Combination3DChart : modern.normalChart,

			// historyChart
			HistoryChart : modern.historyChart,

			// theme4columnBarSeries
			columnBarSeries : modern.columnBarSeries,
			
			// wingColumnChart
			Column2DWingChart : modern.wingColumnChart,

			// barChart
			Bar2DWingChart : modern.barChart,

			RealTimeChart : modern.realTimeChart,

			VTarget3DGoalSeries : modern.targetGoalSeries,
			HTarget3DGoalSeries : modern.targetGoalSeries,
			
			// circular
			circularGauge : modern.circular,

			//  halfCircular
			HalfCircularGauge : modern.halfCircular,

			// cylinderGauge
			VCylinderGauge : modern.cylinderGauge,
			HCylinderGauge : modern.cylinderGauge,
			
			// linearGauge
			VlinearGauge : modern.linearGauge,
			HlinearGauge : modern.linearGauge,
			
			// Series
			Line2DSeries : modern.lineSeries,
			Area2DSeries : modern.areaSeries,
			Column2DSeries : modern.columnBarSeries,
			Bar2DSeries : modern.columnBarSeries,
			
			// Elements
			GridLines : {
				direction : "none" // 배경 없음
			}
		};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//		Lovely Theme
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	rMateChartH5.setupTheme.lovely = {
		defaultColors : [
			"#F85792","#7A6959","#FE8011","#E7DABB","#69DBC4","#3A576F","#219BD1","#78C7EB","#A94A92","#EAA6D5","#FF615E","#00D4A9","#96EDF0","#01D3D5",
			"#009E8E","#A59687","#E40050","#F2558A","#F59BBD","#F38400","#E55201","#E4C191","#44DABC","#00C590","#928679","#E5D1A3","#F34E9A","#F59BCC"
		],

		// Chart Common
		chartCommon : {
			showDataTipTargets : false,
			dataTipBorderStyle : "none",
			dataTipStyleName : {
				color : "#ffffff",
				backgroundColor : "#454545"
			}
		},

		// Normal Chart
		normalChart : {
			gutterLeft : 17,
			gutterRight : 17,
			horizontalAxisRenderer : {
				showLine : false
			},
			verticalAxisRenderer : {
				labelGap : 0,
				showLine : false,
				showLabels : false,
				tickPlacement : "none"
			},
			verticalAxisRenderers : [{
				labelGap : 0,
				showLine : false,
				showLabels : false,
				tickPlacement : "none"
			},{
				labelGap : 0,
				showLine : false,
				showLabels : false,
				tickPlacement : "none"
			}]
		},

		// Normal 3D Chart
		normal3DChart : {
			horizontalAxisRenderer : {
				showLine : true
			}
		},

		// Line 2D Chart
		lineChart : {
			series : [
			]
		},

		// Area 2D Chart
		areaChart : {
			series :[
			]
		},

		// Bar 2D Chart
		barChart : {
			horizontalAxisRenderer : {
				showLine :false,
				showLabels : false,
				labelGap : 0
			},
			horizontalAxisRenderers : [{
				showLine :false,
				showLabels : false,
				labelGap : 0
			},{
				showLine :false,
				showLabels : false,
				labelGap : 0
			}],
			verticalAxisRenderer : {
				showLine : false
			}
		},

		// Bar 3D Chart
		bar3DChart : {
			verticalAxisRenderer : {
				showLine : true
			},
			horizontalAxisRenderer : {
				labelGap : 0,
				showLine : false,
				showLabels : false
			},
			horizontalAxisRenderers : [{
				labelGap : 0,
				showLine : false,
				showLabels : false
			},{
				labelGap : 0,
				showLine : false,
				showLabels : false
			}]
		},

		historyChart : {
			navigator : {
				gutterLeft : 0
			}
		},

		// Wing Column 2D Chart
		wingColumnChart : {
			gutterLeft : 0,
			gutterRight : 0,
			horizontalAxisRenderer : {
				showLine : false
			},
			verticalAxisRenderers : [{
				labelGap : 0,
				showLine : false,
				showLabels : false
			},{
				labelGap : 0,
				showLine : false,
				showLabels : false
			}]
		},

		// Wing Bar 2D Chart
		wingBarChart : {},

		// Line 2D Series
		lineSeries : {
			itemRenderer : "CircleItemRenderer"
		},

		// Area 2D Series
		areaSeries : {
			form : "segment",
			itemRenderer : "CircleItemRenderer"
		},

		// Column Bar 2D Series 
		columnBarSeries : {
			itemRenderer : "BoxItemRenderer",
			halfWidthOffset : 0.5
		},

		targetGoalSeries : {
			stroke : {"color":"#A6ADD7", "weight":1},
			fill : {"color":"#A6ADD7"}
		},

		// circular Gauge Common
		circularCommon : {
			frameStroke : {"color":"#E40050", "weight":10},
			frameFill : {"color":"#96EDF0"},
			needleStroke : {"color":"#F85792"},
			needleFill : {"color":"#F85792", "alpha":1},
			needleCoverFill : {"color":"#414C75"},
			needleCoverStroke : {"color":"#414C75", "weight":3},
			minorTickFill : {"color":"#FF615E"},
			minorTickStroke : {"color":"#FF615E"},
			tickFill : {"color":"#F59BCC"},
			tickStroke : {"color":"#F85792"},
			tickGap : 28,
			labelGap : -34,
			needleLengthRatio : 0.6,
			needleBackLengthRatio : 0.3,
			needleCoverRadius : 0,
			tickLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				fontSize : 14,
				color : "#454545"
			},
			valueLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				color : "#454545",
				fontSize : 14,
				borderStyle : "none",
				borderThickness : 2,
				borderColor : "#3B4B5B"
			},
			valueXOffset : 0,
			needleThickness : 15,
			hideTickLabel : "none" // 틱 라벨을 숨기지 않게 설정합니다.
		},

		// circular Gauge
		circular : {
			minimumAngle : 20,
			maximumAngle : 340,
			valueYOffset : 70
		},

		// Half circular Gauge
		halfCircular : {
			minimumAngle : 190,
			maximumAngle : 350,
			valueYOffset : -50
		},


		// VCylinder, HCylinder Gauge
		cylinderGauge : {
			cylinderColor : ["#E40050", "#F2558A", "#E40050"],
			targetMarkColor : ["#F59BBD", "#F59BBD", "#F59BBD"]
		},

		// VLinear, HLinear Gauge
		linearGauge : {
			linearBorderColor : "#F85792",
			linearColor : ["#F85792", "#F85792", "#F85792"],
			linearBgBorderColor : "#7A6959",
			linearBgColor : ["#7A6959", "#7A6959", "#7A6959"],
			linearBgAlpha : [1, 1, 1],
			targetMarkBorderColor : "#FE8011",
			targetMarkColor : ["#FE8011", "#FE8011", "#FE8011"],
			valueLabelStyleName : {
				fontSize : 13,
				color : "#ffffff",
				fontWeight : "bold"
			},
			tickColor : "#00C590",
			tickLabelStyleName : {
				fontSize : 13
			}
		}
	};
	
	// lovely 테마의 기본 스타일을 저장합니다.
	lovely = rMateChartH5.setupTheme.lovely;

	// Normal Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.normalChart, lovely.chartCommon);

	// Normal 3D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.normal3DChart, lovely.normalChart);

	// Line 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.lineChart, lovely.normalChart);

	// Area 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.areaChart, lovely.normalChart);

	// Bar 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.barChart, lovely.chartCommon);

	// Bar 3D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.bar3DChart, lovely.chartCommon);
	
	// History Chart에 normalChart의 테마를 저장합니다.
	themeExtend(lovely.historyChart, lovely.normalChart);

	// Wing Column 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(lovely.wingColumnChart, lovely.chartCommon);

	// Wing Bar 2D Chart에 wingBarChart의 테마를 저장합니다.
	themeExtend(lovely.wingBarChart, lovely.barChart);

	// circular Gauge에 circularCommon의 테마를 저장합니다.
	themeExtend(lovely.circular, lovely.circularCommon);

	// Half circular Gauge에 circularCommon의 테마를 저장합니다.
	themeExtend(lovely.halfCircular, lovely.circularCommon);

	// Line Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", lovely.lineChart, lovely.defaultColors, 0.5, 4);

	// Line Chart의 series에 fill을 설정합니다.
	intoProp("fill", lovely.lineChart, lovely.defaultColors, 1);

	// Line Chart의 series에 declineFill을 설정합니다.
	intoProp("declineFill", lovely.lineChart, ["#00c0c7"], 1);

	// Line Chart의 series에 declineStroke를 설정합니다.
	intoProp("declineStroke", lovely.lineChart, ["#00c0c7"], 0.5, 4);

	// Area Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", lovely.areaChart, lovely.defaultColors, 0.5, 4);

	// Area Chart의 series에 fill을 설정합니다.
	intoProp("fill", lovely.areaChart, lovely.defaultColors, 1, 2, true);

	// Area Chart의 series에 areaFill을 설정합니다.
	intoProp("areaFill", lovely.areaChart, lovely.defaultColors, 0.5, 2, true);

	// Area Chart의 series에 declineStroke를 설정합니다.
	intoProp("declineStroke", lovely.areaChart, ["#00c0c7"], 0.5, 4);

	// Area Chart의 series에 declineFill을 설정합니다.
	intoProp("declineFill", lovely.areaChart, ["#00c0c7"], 1, 2, true);

	//--------------------------------------------------------------------------------
	//
	//	실제적인 lovely Theme
	//
	//--------------------------------------------------------------------------------
	rMateChartH5.themes.lovely = {
			// default color
		defaultColors : lovely.defaultColors,

			// rMateChart 
		rMateChart : {
			paddingTop :0,
			paddingLeft : 0,
			paddingRight : 0,
			background : "url(../rMateChartH5/Assets/Images/theme4_bg.png)",
			border : "solid 1px #e5e5e5"
		},
		
			// Caption
		Caption : {
			backgroundColor : "#000000",
			borderRadius : 5,
			color : "#ffffff",
			width : "100%",
			height : 20,
			paddingTop : 12,
			paddingBottom : 13,
			margin : "0 auto"
		},
		
			// Axis2DRenderer - x axis, y axis
		Axis2DRenderer : {
			fontSize : 13,
			color : "#333333"
		},

		// normalChart
		Column2DChart : lovely.normalChart,
		Pie2DChart : lovely.normalChart,
		Pie3DChart : lovely.normalChart,
		Plot2DChart : lovely.normalChart,
		Combination2DChart : lovely.normalChart,
		RadarChart : lovely.normalChart,
		RealTimeChart : lovely.normalChart,
		Matrix2DChart : lovely.normalChart,
		ImageChart : lovely.normalChart,
		Candlestick2DChart : lovely.normalChart,
		CandleLine2DChart : lovely.normalChart,
		CandleArea2DChart : lovely.normalChart,

		// normal3DChart
		Column3DChart : lovely.normal3DChart,
		Combination3DChart : lovely.normal3DChart,
		Bubble3DChart : lovely.normal3DChart,
		
		// lineChart
		Line2DChart : lovely.lineChart,
		
		// areaChart
		Area2DChart : lovely.areaChart,
		
		// barChart
		Bar2DChart : lovely.barChart,
		
		// bar3DChart
		bar3DChart : lovely.bar3DChart,

		// historyChart
		HistoryChart : lovely.historyChart,

		// wingColumnChart
		Column2DWingChart : lovely.wingColumnChart,
		
		// barChart
		Bar2DWingChart : lovely.barChart,

		// circular
		CircularGauge : lovely.circular,
		
		// halfCircular
		HalfCircularGauge : lovely.halfCircular,

		// cylinderGauge
		VCylinderGauge : lovely.cylinderGauge,
		HCylinderGauge : lovely.cylinderGauge,

		// linearGauge
		VlinearGauge : lovely.linearGauge,
		HlinearGauge : lovely.linearGauge,

		// Series
		Column2DSeries : lovely.columnBarSeries,
		Bar2DSeries : lovely.columnBarSeries,
		Line2DSeries : lovely.lineSeries,
		Area2DSeries : lovely.areaSeries,

		VTarget3DGoalSeries : lovely.targetGoalSeries,
		HTarget3DGoalSeries : lovely.targetGoalSeries,

		// Elements
		GridLines : {
			direction : "none" // 배경 선 안보이게
		}
	};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//		Pastel Theme
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	rMateChartH5.setupTheme.pastel = {
		defaultColors : [
			"#C380B5","#B5DCED","#74B3CD","#655C71","#DAD4BC","#006970","#12AE98","#6CC0C0","#C5C5C5","#1F94CA","#6CABE4","#E6C1DB","#904466","#BA4D84",
			"#E86991","#ED9093","#CE7B67","#AB9B55","#B0AC5E","#DCD88C","#9168CE","#6F5AC8","#548AC8","#5CB2D5","#70D6D4","#DFDFDF","#B282B7","#DEA3E5"
		],

		// Chart Common
		chartCommon : {
			dataTipStyleName : {
				color : "#ffffff"
			},
			showDataTipTargets : false,
			dataTipBackgroundColorOnSeries : true,
			dataTipBorderColor : "#ffffff"
		},

		// Line 2D Chart
		lineChart : {
			series : [
			]
		},

		// Area 2D Chart
		areaChart : {
			series : [
			]
		},

		// Bar 2D Chart
		barChart : {
			backgroundElements : [{
				direction : "both",
				horizontalFill : {"color":"#ffffff"},
				verticalStroke : {"color":"#ececec"},
				verticalFill : {"color":"#f9f9f9", "alpha":0.5},
				horizontalStroke : {"color":"#ececec"}
			}]
		},

		// Line 2D Series
		lineSeries : {
			itemRenderer : "CircleItemRenderer"
		},

		// Area 2D Series
		areaSeries : {
			form : "segment",
			itemRenderer : "CircleItemRenderer"
		},

		// Column Bar 2D Series 
		columnBarSeries : {
			itemRenderer : "BoxItemRenderer"
		},

		// Combination Chart
		combinationChart : {
			series : [
			]
		},

		// Target Goal Series
		targetGoalSeries : {
			stroke : {"color":"#B5DCED", "weight":1},
			fill : {"color":"#B5DCED"}
		},

		// Real Time Chart
		realTimeChart : {
			series : []
		},

		// circular Common
		circularCommon : {
			frameStroke : {"color":"#B5DCED", "weight":8},
			frameFill : {"color":"#74B3CD"},
			needleStroke : {"color":"#1F94CA"},
			needleFill : {"color":"#6CABE4", "alpha":1},
			needleCoverFill : {"color":"#E86991"},
			needleCoverStroke : {"color":"#414C75", "weight":3},
			minorTickFill : {"color":"#B5DCED"},
			minorTickStroke : {"color":"#B5DCED"},
			tickFill : {"color":"#1F94CA"},
			tickStroke : {"color":"#1F94CA", "weight":1},
			tickGap : 8,
			labelGap : 14,
			needleLengthRatio : 0.6,
			needleBackLengthRatio : 0.3,
			needleCoverRadius : 0,
			tickLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				fontSize : 14,
				color : "#655C71"
			},
			valueLabelStyleName : {
				fontFamily : "arial,맑은 고딕",
				color : "#454545",
				fontSize : 14,
				borderStyle : "none",
				borderThickness : 2,
				borderColor : "#3B4B5B"
			},
			valueXOffset : 0,
			needleThickness : 10,
			hideTickLabel : "none" // 틱 라벨을 숨기지 않게 설정합니다.
		},

		// circular Gauge
		circular : {
			minimumAngle : 30,
			maximumAngle : 330,
			valueYOffset : 100
		},

		// Half circular Gauge		
		halfCircular : {
			minimumAngle : 190,
			maximumAngle : 350,
			valueYOffset : -50
		},

		// VCylinder, HCylinder Gauge
		cylinderGauge : {
			cylinderColor : ["#B282B7", "#DEA3E5", "#B282B7"],
			targetMarkColor : ["#C380B5", "#C380B5", "#C380B5"],
			color : "#B5DCED",
			tickLabelStyleName : {
				fontSize : 12
			},
			valueLabelStyleName : {
				fontSize : 12,
				fontWeight : "bold"
			}
		},

		// VLinear, HLinear Gauge
		linearGauge : {
			linearBorderColor : "#C380B5",
			linearColor : ["#C380B5", "#C380B5", "#C380B5"],
			linearAlpha : [0.5, 0.8, 1],
			linearBgBorderColor : "#74B3CD",
			linearBgColor : ["#74B3CD", "#74B3CD", "#74B3CD"],
			targetMarkBorderColor : "#548AC8",
			targetMarkColor : ["#548AC8", "#548AC8", "#548AC8"],
			targetMarkAlpha : [0.5, 0.8, 1],
			valueLabelStyleName : {
				fontSize : 13,
				color : "#ffffff"
			},
			tickLabelStyleName : {
				fontSize : 12
			}
		}
	};
	
	// pastel 테마의 기본 스타일을 저장합니다.
	pastel = rMateChartH5.setupTheme.pastel;

	// Line 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(pastel.lineChart, pastel.chartCommon);

	// Area 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(pastel.areaChart, pastel.chartCommon);

	// Bar 2D Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(pastel.barChart, pastel.chartCommon);

	// Combination Chart에 chartCommon의 테마를 저장합니다.
	themeExtend(pastel.combinationChart, pastel.chartCommon);

	// circular Gauge에 circulartCommon의 테마를 저장합니다.
	themeExtend(pastel.circular, pastel.circularCommon);

	// Half circular Guage에 circularCommon의 테마를 저장합니다.
	themeExtend(pastel.halfCircular, pastel.circularCommon);

	// Line 2D Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", pastel.lineChart, pastel.defaultColors, 0.5, 4);

	// Line 2D Chart의 series에 fill를 설정합니다.
	intoProp("fill", pastel.lineChart, pastel.defaultColors, 1);

	// Line 2D Chart의 series에 declineStroke를 설정합니다.
	intoProp("declineStroke", pastel.lineChart, ["#00c0c7"], 0.5, 4);

	// Line 2D Chart의 series에 declineFill을 설정합니다.
	intoProp("declineFill", pastel.lineChart, ["#00c0c7"], 1);

	// Area 2D Chart의 series에 stroke를 설정합니다.
	intoProp("stroke", pastel.areaChart, pastel.defaultColors, 0.5, 4);

	// Area 2D Chart의 series에 fill를 설정합니다.
	intoProp("fill", pastel.areaChart, pastel.defaultColors, 1);

	// Area 2D Chart의 series에 declineStroke를 설정합니다.
	intoProp("declineStroke", pastel.areaChart, ["#00c0c7"], 0.5, 4);

	// Area 2D Chart의 series에 declineFill을 설정합니다.
	intoProp("declineFill", pastel.areaChart, ["#00c0c7"], 1);

	// RealTimeChart의 series에 fill을 설정합니다.
	intoProp("fill", pastel.realTimeChart, pastel.defaultColors, 0.5);

	//--------------------------------------------------------------------------------
	//
	//	실제적인 pastel Theme
	//
	//--------------------------------------------------------------------------------
	rMateChartH5.themes.pastel = {
		// default color
		defaultColors : pastel.defaultColors,
		
		// Axis2DRenderer - x axis, y axis
		Axis2DRenderer : {
			showLine : true,
			axisStroke : {"color":"#000000", "weight":4}
		},

		// chartCommon
		Column2DChart : pastel.chartCommon,
		Column3DChart : pastel.chartCommon,
		Pie2DChart : pastel.chartCommon,
		Pie3DChart : pastel.chartCommon,
		Bubble3DChart : pastel.chartCommon,
		Plot2DChart : pastel.chartCommon,
		RadarChart : pastel.chartCommon,
		HistoryChart : pastel.chartCommon,
		Matrix2DChart : pastel.chartCommon,
		ImageChart : pastel.chartCommon,
		Column2DWingChart : pastel.chartCommon,
		Bar2DWingChart : pastel.chartCommon,
		Candlestick2DChart : pastel.chartCommon,
		CandleLine2DChart : pastel.chartCommon,
		CandleArea2DChart : pastel.chartCommon,

		// lineChart
		Line2DChart : pastel.lineChart,
		
		// areaChart
		Area2DChart : pastel.areaChart,
		
		// barChart
		Bar2DChart : pastel.barChart,
		Bar3DChart : pastel.barChart,

		// realTimeChart
		RealTimeChart : pastel.realTimeChart,

		// CombinationChart
		Combination2DChart : pastel.combinationChart,
		//Combination3DChart : pastel.combinationChart,
		Combination3DChart : pastel.chartCommon,

		VTarget3DGoalSeries : pastel.targetGoalSeries,
		HTarget3DGoalSeries : pastel.targetGoalSeries,

		// circular
		CircularGauge : pastel.circular,

		// halfCircular
		HalfCircularGauge : pastel.halfCircular,

		// cylinderGauge
		VCylinderGauge : pastel.cylinderGauge,
		HCylinderGauge : pastel.cylinderGauge,

		// linearGauge
		VlinearGauge : pastel.linearGauge,
		HlinearGauge : pastel.linearGauge,

		// Series
		Line2DSeries : pastel.lineSeries,
		Area2DSeries : pastel.areaSeries,
		Column2DSeries : pastel.columnBarSeries,
		Bar2DSeries : pastel.columnBarSeries,

		// Elements
		GridLines : {
			direction : "both",
			horizontalStroke : {"color":"#ececec"},
			horizontalFill : {"color":"#f9f9f9"},
			horizontalAlternateFill : {"color":"#ffffff"},
			verticalStroke : {"color":"#ececec"}
		}
	};
})();