import SignerForm from "@/components/signer-form"

export default function Signer(){
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        <SignerForm />
      </div>
    </div>
  )
}