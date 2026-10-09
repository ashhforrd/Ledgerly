import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

export type SelectOption = { value: string; label: string };

export function FormSelect({
  name,
  label,
  options,
  defaultValue,
  value,
  onValueChange,
  placeholder = "Select an option",
  required = false,
  disabled = false,
}: {
  name?: string;
  label?: string;
  options: SelectOption[];
  defaultValue?: string;
  value?: string;
  onValueChange?: (value: string) => void;
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
}) {
  return (
    <div className="space-y-2">
      {label && <Label>{label}</Label>}
      <Select
        name={name}
        value={value}
        defaultValue={defaultValue}
        onValueChange={(next) => {
          if (next) onValueChange?.(next);
        }}
        required={required}
        disabled={disabled}
      >
        <SelectTrigger className="h-10 w-full px-3">
          <SelectValue placeholder={placeholder} />
        </SelectTrigger>
        <SelectContent align="start">
          {options.map((option) => (
            <SelectItem key={option.value} value={option.value}>
              {option.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
