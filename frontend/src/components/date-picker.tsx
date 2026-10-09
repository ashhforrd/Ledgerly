import { lazy, Suspense, useState } from "react";
import { CalendarDays } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

const Calendar = lazy(() =>
  import("@/components/ui/calendar").then((module) => ({
    default: module.Calendar,
  })),
);

function toDate(value?: string) {
  return value ? new Date(`${value}T12:00:00`) : undefined;
}

function toValue(value?: Date) {
  if (!value) return "";
  const year = value.getFullYear();
  const month = String(value.getMonth() + 1).padStart(2, "0");
  const day = String(value.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function DatePicker({
  name,
  label,
  defaultValue,
  required = false,
}: {
  name: string;
  label: string;
  defaultValue?: string;
  required?: boolean;
}) {
  const [date, setDate] = useState<Date | undefined>(toDate(defaultValue));
  const [open, setOpen] = useState(false);
  const value = toValue(date);

  return (
    <div className="space-y-2">
      <Label>{label}</Label>
      <input type="hidden" name={name} value={value} required={required} />
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger
          render={
            <Button
              type="button"
              variant="outline"
              className="h-10 w-full justify-start px-3 font-normal"
            />
          }
        >
          <CalendarDays className="text-muted-foreground" />
          <span className={value ? "" : "text-muted-foreground"}>
            {value || "Choose date"}
          </span>
        </PopoverTrigger>
        <PopoverContent align="start" className="w-auto">
          <Suspense
            fallback={
              <div className="h-[276px] w-[252px] animate-pulse rounded-lg bg-muted" />
            }
          >
            <Calendar
              mode="single"
              selected={date}
              onSelect={(next) => {
                setDate(next);
                setOpen(false);
              }}
            />
          </Suspense>
        </PopoverContent>
      </Popover>
    </div>
  );
}
