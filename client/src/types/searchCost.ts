export interface SearchCost {
    costType: string;
    cost: number;
    expiredAtDiscount?: string;
}

export interface SearchCostResponse {
    success: boolean;
    data: SearchCost;
}