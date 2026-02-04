export interface TransactionLog{
  id: number;
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  transactionDate:string;
  description: string;
}
