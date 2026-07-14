import { Component, createEffect, createSignal, JSX, onCleanup, Show, Suspense } from "solid-js";
import { HtmlIdFamily } from "../htmlIds";
import { useApi, useTranslator } from "~/src/app-context/AppContext";
import { createAsync, query } from "@solidjs/router";
import ImgSkeleton from "../skeletons/ImgSkeleton";
import { isAllowedFileType, isWithinSizeLimit, SizeUnit } from "@/utils/fileValidation";

type Props = {
  name?: string;
  label?: string;
  fileTypes: string[];
  maxSize: number;
  maxSizeUnit: SizeUnit;
  putEndpoint: string;
  currentValue?: {
    url: string;
    label?: string;
  };
};

const FileInputDragDrop: Component<Props> = (props) => {
  const t = useTranslator();
  const idFam = new HtmlIdFamily("file-input");

  let inputRef!: HTMLInputElement;

  const remoteFile = createAsync(async () => {
    if (props.currentValue) {
      const blob = await currenFilePreviewBlobQuery(props.currentValue.url);
      if (!blob) return undefined;
      return new File([blob], props.currentValue.label || "", { type: blob.type });
    }
  });
  const [localFile, setLocalFile] = createSignal<File | null>(null);

  const file = () => localFile() ?? remoteFile();

  const rejectFile = (error: string) => {
    setError(error);
    setLocalFile(null);
    inputRef.files = null;
  };

  const processFile = (file: File | undefined) => {
    if (!file) return;

    setDropped(true);
    setDragOver(false);
    setError(null);

    if (!isAllowedFileType(file.type, props.fileTypes)) {
      rejectFile(t("global.fileDragDrop.fileTypeNotSupported"));
      return;
    }

    if (!isWithinSizeLimit(file.size, props.maxSize, props.maxSizeUnit)) {
      rejectFile(t("global.fileDragDrop.fileTooLarge"));
      return;
    }

    setLocalFile(file);
  };

  const preventDefaultBehavior: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const [dropped, setDropped] = createSignal(false);
  const [error, setError] = createSignal<string | null>(null);
  const handleDrop: JSX.EventHandlerUnion<HTMLDivElement, DragEvent> = (e) => {
    preventDefaultBehavior(e);
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
              type="file"
              class="sr-only"
              name={props.name}
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
        <FilePreview file={file()} dragOver={dragOver()} />
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

const FilePreview: Component<{ a?: string; file: File | undefined; dragOver: boolean }> = (props) => {
  const [url, setUrl] = createSignal<string>();
  const isImage = () => props.file?.type.startsWith("image/");

  createEffect(() => {
    if (!props.file) {
      setUrl(undefined);
      return;
    }

    const objectUrl = URL.createObjectURL(props.file);
    setUrl(objectUrl);

    onCleanup(() => URL.revokeObjectURL(objectUrl));
  });

  return (
    <>
      <Suspense
        fallback={
          <div class="h-48 w-64">
            <ImgSkeleton />
          </div>
        }
      >
        <Show when={isImage() && url()}>
          <div class="mt-3 flex justify-center pointer-events-none">
            <img
              src={url()!}
              alt="Preview"
              class="max-h-48 rounded-radius shadow-md border border-outline dark:border-outline-dark transition-all duration-200"
            />
          </div>
        </Show>
      </Suspense>

      {/* FIX: Idk what this is. It also has a repeated `when` condition for separate `Show` components. */}
      <Suspense fallback={<p>Loading preview...</p>}>
        <Show when={url() && !isImage()}>
          <div class="mt-3 flex justify-center" classList={{ "pointer-events-none": props.dragOver }}>
            <Show when={props.file}>{(f) => <a href={url()!}>{f().name}</a>}</Show>
            <Show when={props.file}>
              <a href={url()!}>{props.file?.name}</a>
            </Show>
          </div>
        </Show>
      </Suspense>
    </>
  );
};

const currenFilePreviewBlobQuery = query(async (src: string) => {
  const res = await useApi().authFetch(src);
  if (!res.ok) return null;
  return res.blob();
}, "current-file-preview-blob");
