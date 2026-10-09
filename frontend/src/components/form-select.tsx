import { useState } from "react";
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
  const [internalValue, setInternalValue] = useState(defaultValue ?? "");
  const selectedValue = value ?? internalValue;
  const selectedLabel = options.find(
    (option) => option.value === selectedValue,
  )?.label;

  return (
    <div className="space-y-2">
      {label && <Label>{label}</Label>}
      <Select
        name={name}
        value={selectedValue}
        onValueChange={(next) => {
          if (!next) return;
          if (value === undefined) setInternalValue(next);
          onValueChange?.(next);
        }}
        required={required}
        disabled={disabled}
      >
        <SelectTrigger className="h-10 w-full px-3">
          <SelectValue placeholder={placeholder}>{selectedLabel}</SelectValue>
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
