import { ErrorBoundary, ParentComponent } from "solid-js";
import { FaSolidTriangleExclamation } from "solid-icons/fa";
import Button from "./Button";
import { useTranslator } from "@/app-context/AppContext";
import { useNavigate } from "@solidjs/router";

const ParentErrorBoundary: ParentComponent = (props) => {
  const t = useTranslator();
  const navigate = useNavigate();

  return (
    <ErrorBoundary
      fallback={(_error, reset) => (
        <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
          <div class="w-full max-w-lg rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
            <div class="flex items-center gap-3">
              <span class="flex size-10 shrink-0 items-center justify-center rounded-full bg-danger/10 text-danger dark:bg-red-400/10 dark:text-red-400">
                <FaSolidTriangleExclamation size={18} />
              </span>
              <h1 class="text-xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
                {t("errors.boundary.title")}
              </h1>
            </div>

            <Button
              text={t("errors.boundary.goBack")}
              onClick={() => {
                reset();
                navigate("/");
              }}
              category="primary"
              class="mt-6 w-full"
            />
          </div>
        </div>
      )}
    >
      {props.children}
    </ErrorBoundary>
  );
};

export default ParentErrorBoundary;
