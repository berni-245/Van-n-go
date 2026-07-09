import { render } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import Button from "@/components/Button";

const user = userEvent.setup();

test("renders its text and category-based classes", () => {
  const { getByRole } = render(() => (
    <Button category="primary" text="Save" />
  ));

  const button = getByRole("button");
  expect(button).toHaveTextContent("Save");
  expect(button).toHaveClass("bg-primary");
});

test("calls onClick when clicked", async () => {
  const handleClick = vi.fn();
  const { getByRole } = render(() => (
    <Button category="primary" text="Save" onClick={handleClick} />
  ));

  await user.click(getByRole("button"));

  expect(handleClick).toHaveBeenCalledOnce();
});

test("is disabled when the disabled prop is set", () => {
  const { getByRole } = render(() => (
    <Button category="danger" text="Delete" disabled />
  ));

  expect(getByRole("button")).toBeDisabled();
});
