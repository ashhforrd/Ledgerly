const API_BASE = '/api/v1'

export class ApiError extends Error {
  constructor(public status: number, message: string) { super(message) }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('ledgerly.accessToken')
  const headers = new Headers(options.headers)
  if (!(options.body instanceof FormData)) headers.set('Content-Type', 'application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (response.status === 204) return undefined as T
  const body = await response.json().catch(() => null)
  if (!response.ok) throw new ApiError(response.status, body?.detail ?? 'The request could not be completed.')
  return body as T
}

export type Workspace = { id: string; name: string; defaultCurrency: string; timezone: string; role: string }
export type Category = { id: string; name: string; type: 'INCOME'|'EXPENSE'; active: boolean }
export type Transaction = { id:string; categoryName:string|null; type:'INCOME'|'EXPENSE'; status:'POSTED'|'VOIDED'; amount:number; currency:string; occurredOn:string; description:string }
export type TransactionPage = { items:Transaction[]; totalItems:number; totalPages:number; page:number; size:number }
export type Customer = { id:string; name:string; email:string|null; phone:string|null }
export type Invoice = { id:string; customerName:string; invoiceNumber:string; status:string; currency:string; issueDate:string; dueDate:string; total:number; paidAmount:number; outstanding:number; overdue:boolean }
export type Report = { from:string; to:string; currency:string; income:number; expenses:number; netCashFlow:number; transactionCount:number; categories:{type:string;category:string;amount:number}[] }
export type Rule = { id:string; name:string; categoryId:string; categoryName:string; type:string; descriptionContains:string; priority:number; active:boolean }

export function money(value:number,currency:string){return new Intl.NumberFormat(undefined,{style:'currency',currency,maximumFractionDigits:2}).format(value)}
