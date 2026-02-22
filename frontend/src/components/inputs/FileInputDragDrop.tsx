import { Component, createSignal, JSX, Show } from "solid-js";
import { HtmlIdFamily } from "../htmlIds";
import { useTranslator } from "~/src/app-context/AppContext";

type SizeUnits = "B" | "KB" | "MB" | "GB" | "TB";

type Props = {
  name?: string;
  label?: string;
  fileTypes: string[];
  maxSize: number;
  maxSizeUnit: SizeUnits;
  onFileSelected?: (file: File | null) => void;
};

const SizeMultiplier: Record<SizeUnits, number> = {
  B: 1,
  KB: 2 ** 10,
  MB: 2 ** 11,
  GB: 2 ** 12,
  TB: 2 ** 13,
} as const;

const FileInputDragDrop: Component<Props> = (props) => {
  const t = useTranslator();
  const idFam = new HtmlIdFamily("file-input");

  let inputRef!: HTMLInputElement;

  const maxSizeBytes = props.maxSize * SizeMultiplier[props.maxSizeUnit];

  const [previewUrl, setPreviewUrl] = createSignal<string | null>(null);

  const clearPreview = () => {
    const current = previewUrl();
    if (current) {
      URL.revokeObjectURL(current);
      setPreviewUrl(null);
    }
  };

  const rejectFile = (error: string) => {
    clearPreview();
    setError(error);
    inputRef.files = null;
  };

  const processFile = (file: File | undefined) => {
    if (!file) return;

    setDropped(true);
    setDragOver(false);
    setError(null);

    const fileTypes = props.fileTypes.map((ft) => ft.toLowerCase());
    const fileType = file.type.split("/").pop()!.toLowerCase();
    if (!fileTypes.includes(fileType)) {
      rejectFile(t("global.fileDragDrop.fileTypeNotSupported"));
      return;
    }

    if (file.size > maxSizeBytes) {
      rejectFile(t("global.fileDragDrop.fileTooLarge"));
      return;
    }

    if (file.type.startsWith("image/")) {
      const url = URL.createObjectURL(file);
      setPreviewUrl(url);
    }

    props.onFileSelected?.(file);
  };

  const preventDefaultBehavior: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const [dropped, setDropped] = createSignal(false);
  const [error, setError] = createSignal<string | null>(null);
  const handleDrop: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    preventDefaultBehavior(e);
    // console.log("Drop", e);
    const files = e.dataTransfer?.files;
    if (files && files.length > 0) {
      inputRef.files = files;
      processFile(files[0]);
    }
  };

  const [dragOver, setDragOver] = createSignal(false);
  const handleEnter: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    preventDefaultBehavior(e);
    setDragOver(true);
    console.log("enter");
  };
  const handleLeave: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    preventDefaultBehavior(e);
    setDragOver(false);
    console.log("leave");
  };

  return (
    <div class="flex w-full max-w-xl text-center flex-col gap-1">
      <span
        class="w-fit pl-0.5 text-sm transition-all duration-200 ease-out"
        classList={{
          "text-on-surface dark:text-on-surface-dark": !dropped() && !error(),
          "scale-110": dragOver(),
          "text-success scale-105": dropped() && !dragOver() && !error(),
          "text-danger scale-105": dropped() && !dragOver() && !!error(),
        }}
      >
        {props.label}
      </span>
      <div
        onDrop={handleDrop}
        onDragOver={preventDefaultBehavior}
        onDragEnter={handleEnter}
        onDragLeave={handleLeave}
        class="flex w-full flex-col items-center justify-center gap-2 rounded-radius border border-dashed p-8 text-on-surface transition-all duration-200 ease-out dark:text-on-surface-dark"
        classList={{
          "border-outline dark:border-outline-dark": !dragOver() && !dropped() && !error(),
          "border-primary bg-primary/10 ring-2 ring-primary/30 scale-[1.01] dark:border-primary-dark dark:bg-primary-dark/10":
            dragOver(),
          "border-success bg-success/10 ring-2 ring-success/30": dropped() && !dragOver() && !error(),
          "border-danger bg-danger/10 ring-2 ring-danger/30": dropped() && !dragOver() && !!error(),
        }}
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          aria-hidden="true"
          fill="currentColor"
          class="w-12 h-12 opacity-75 transition-all duration-200 ease-out pointer-events-none"
          classList={{
            "text-primary scale-110": dragOver(),
            "text-success scale-105": dropped() && !dragOver() && !error(),
            "text-danger scale-105": dropped() && !dragOver() && !!error(),
          }}
        >
          <path
            fill-rule="evenodd"
            d="M10.5 3.75a6 6 0 0 0-5.98 6.496A5.25 5.25 0 0 0 6.75 20.25H18a4.5 4.5 0 0 0 2.206-8.423 3.75 3.75 0 0 0-4.133-4.303A6.001 6.001 0 0 0 10.5 3.75Zm2.03 5.47a.75.75 0 0 0-1.06 0l-3 3a.75.75 0 1 0 1.06 1.06l1.72-1.72v4.94a.75.75 0 0 0 1.5 0v-4.94l1.72 1.72a.75.75 0 1 0 1.06-1.06l-3-3Z"
            clip-rule="evenodd"
          />
        </svg>
        <div class="group" classList={{ "pointer-events-none": dragOver() }}>
          <label
            for={idFam.id("input")}
            class="font-medium text-primary group-focus-within:underline dark:text-primary-dark hover:cursor-pointer"
            classList={{ "pointer-events-none": dragOver() }}
          >
            <input
              ref={inputRef}
              id={idFam.id("input")}
              name={props.name}
              type="file"
              class="sr-only"
              aria-describedby={idFam.id("description")}
              onChange={(e) => {
                const file = e.currentTarget.files?.[0];
                processFile(file);
              }}
            />
            {t("global.fileDragDrop.browse")}
          </label>
          {" " + t("global.fileDragDrop.orDragAndDrop")}
        </div>
        <small id={idFam.id("description")} class="pointer-events-none">
          {props.fileTypes?.join(", ")} - {t("global.fileDragDrop.max")} {props.maxSize}
          {props.maxSizeUnit}
        </small>

        <Show when={previewUrl()}>
          <div class="mt-3 flex justify-center pointer-events-none">
            <img
              src={previewUrl()!}
              alt="Preview"
              class="max-h-48 rounded-radius shadow-md border border-outline dark:border-outline-dark transition-all duration-200"
            />
          </div>
        </Show>
      </div>
      <Show when={error()}>
        <small
          class="text-danger text-right"
          classList={{
            "text-on-surface/20 dark:text-on-surface-dark/20": dragOver(),
          }}
        >
          {error()}
        </small>
      </Show>
    </div>
  );
};

export default FileInputDragDrop;
