"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import CopyIdSigner from "@/components/input-id-signature"
import { Separator } from "@/components/ui/separator"

export default function SignerForm(){
  const [text, setText] = useState("")
  const [isSigned, setIsSigned] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [signatureId, setSignatureId] = useState<number | null>(null)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsLoading(true)
    setError("")

    try {
      const token = localStorage.getItem("token")
      if (!token) {
        router.push("/sign-in")
        return
      }

      const response = await fetch("http://localhost:8080/api/signatures/sign", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ text }),
      })

      if (response.ok) {
        const data = await response.json()
        setSignatureId(data.signatureId)
        setIsSigned(true)
      } else if (response.status === 401) {
        // Token inválido, redirecionar para login
        localStorage.removeItem("token")
        localStorage.removeItem("user")
        router.push("/sign-in")
      } else {
        const errorData = await response.json()
        setError(errorData.error || "Erro ao assinar documento")
      }
    } catch (error) {
      setError("Erro de conexão com o servidor")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Sign text</CardTitle>
        <CardDescription>
          Enter the content for signature
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
          <div className="flex flex-col gap-6">
            {error && (
              <div className="text-sm text-red-600 bg-red-50 p-3 rounded">
                {error}
              </div>
            )}
            <div className="grid gap-3">
              <Textarea 
                placeholder="Type here"
                value={text}
                onChange={(e) => setText(e.target.value)}
                required
              />
            </div>
            <div className="flex flex-col gap-3">
              <Button 
                type="submit" 
                className="w-full cursor-pointer"
                disabled={isLoading || isSigned}
              >
                {isLoading ? "Signing..." : isSigned ? "Signed" : "Sign"}
              </Button>
              <Button 
                type="button" 
                variant="outline"
                className="w-full cursor-pointer"
                onClick={() => router.push("/verify")}
              >
                Verify Signature
              </Button>
            </div>
          </div>
        </form>
      </CardContent>
      {isSigned && signatureId && (
      <CardFooter>
        <div className="w-full">
          <Separator className="mb-4" />
          <CopyIdSigner signatureId={signatureId} />
        </div>
      </CardFooter>
      )}
    </Card>
  )
}