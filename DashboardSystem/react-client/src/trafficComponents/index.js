import withSplitting from '../withSplitting';


export const TopicChart = withSplitting(() => import('./TopicChart'));
export const AccumulationChart = withSplitting(() => import('./AccumulationChart'));
export const TopicPie = withSplitting(() => import('./TopicPie'));

export const ConnectionInfoChart = withSplitting(() => import('./ConnectionInfoChart'));
