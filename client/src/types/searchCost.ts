export interface SearchCost {
    costType: 'DISCOUNT' | 'FREE' | 'ORIGIN';
    cost: number;
    expiredAtDiscount?: string;
}

export interface SearchCostResponse {
    success: boolean;
    data: SearchCost;
}