import { cn } from "cn";

export function BrandMark({ className = "" }: { className?: string }) {
  return (
    <div
      className={cn(
        "grid size-10 shrink-0 place-items-center rounded-xl bg-blue-500 text-white shadow-lg shadow-blue-500/20",
        className,
      )}
    >
      <svg viewBox="0 0 48 48" className="size-7" aria-hidden="true">
        <path
          d="M10 17 7 8l10 5a18 18 0 0 1 14 0L41 8l-3 9a17 17 0 1 1-28 0Z"
          fill="currentColor"
        />
        <path
          d="M15 23c0-4 3-7 7-7h4c4 0 7 3 7 7 0 8-9 13-9 13s-9-5-9-13Z"
          fill="#0b1220"
          opacity=".9"
        />
        <circle cx="20" cy="23" r="3" fill="currentColor" />
        <circle cx="28" cy="23" r="3" fill="currentColor" />
        <circle cx="20" cy="23" r="1.2" fill="#0b1220" />
        <circle cx="28" cy="23" r="1.2" fill="#0b1220" />
        <path d="m24 25-2.5 3h5L24 25Z" fill="#60a5fa" />
      </svg>
    </div>
  );
}
