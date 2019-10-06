import withSplitting from '../withSplitting';

export const CPU = withSplitting(() => import('./CPU'));
export const Cores = withSplitting(() => import('./Cores'));
export const Memory = withSplitting(() => import('./Memory'));
export const Network = withSplitting(() => import('./Network'));
export const ProcessTable = withSplitting(() => import('./ProcessTable'));
