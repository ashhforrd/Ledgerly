import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'

const foundations = [
  ['API', 'Spring Boot'],
  ['Database', 'PostgreSQL'],
  ['Async processing', 'Redis Streams'],
  ['Interface', 'React and shadcn/ui'],
] as const

function App() {
  return (
    <main className="mx-auto flex min-h-screen w-full max-w-3xl items-center px-6 py-16">
      <section className="w-full space-y-8" aria-labelledby="page-title">
        <header className="space-y-3">
          <Badge variant="outline">Project foundation</Badge>
          <h1 id="page-title" className="text-3xl font-semibold tracking-tight">
            Ledgerly
          </h1>
          <p className="max-w-2xl text-base leading-7 text-muted-foreground">
            Financial operations for small businesses. Track transactions,
            import statements, manage invoices, and understand cash flow.
          </p>
        </header>

        <Card>
          <CardHeader>
            <CardTitle>Development environment</CardTitle>
            <CardDescription>
              The application foundation is ready. Financial features will be
              implemented milestone by milestone.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <dl className="divide-y">
              {foundations.map(([term, description]) => (
                <div
                  key={term}
                  className="grid grid-cols-[minmax(0,1fr)_minmax(0,1.5fr)] gap-4 py-3 first:pt-0 last:pb-0"
                >
                  <dt className="text-sm text-muted-foreground">{term}</dt>
                  <dd className="text-sm font-medium">{description}</dd>
                </div>
              ))}
            </dl>
          </CardContent>
        </Card>
      </section>
    </main>
  )
}

export default App
