"use client"

import { useState } from "react"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Separator } from "./ui/separator"
import { Badge } from "./ui/badge"
import { AlertCircleIcon, BadgeCheckIcon, CheckIcon } from "lucide-react"

interface VerificationResult {
  signatureId: number
  status: string
  signatory: string
  algorithm: string
  signedAt: string
  originalText: string
  verificationCount: number
  valid: boolean
}

export function VerifyForm(){
  const [signatureId, setSignatureId] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [verificationResult, setVerificationResult] = useState<VerificationResult | null>(null)
  
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsLoading(true)
    setError("")
    setVerificationResult(null)

    try {
      const response = await fetch(`http://localhost:8080/api/verify/${signatureId}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })

      if (response.ok) {
        const data = await response.json()
        setVerificationResult(data)
      } else {
        const errorData = await response.json()
        setError(errorData.error || "Erro ao verificar assinatura")
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
          <CardTitle>Signature verification</CardTitle>
          <CardDescription>
            Enter ID or text + signature
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <div className="flex flex-col gap-6">
              <div className="grid gap-3">
                <Label htmlFor="signatureId">Signature ID</Label>
                <Input
                  id="signatureId"
                  type="text"
                  placeholder="Enter signature ID"
                  value={signatureId}
                  onChange={(e) => setSignatureId(e.target.value)}
                  required
                />
              </div>
              
              {error && (
                <div className="text-red-500 text-sm">
                  {error}
                </div>
              )}
              
              <div className="flex flex-col gap-3">
                <Button type="submit" className="w-full cursor-pointer" disabled={isLoading || !signatureId}>
                  {isLoading ? "Verifying..." : "Verify Signature"}
                </Button>
              </div>
            </div>
          </form>
        </CardContent>
      {verificationResult && (
        <CardFooter className="flex">
          <div className="w-full">
            <Separator className="mb-4" />
            <div className="mb-4">
              {verificationResult.valid ? (
                <Badge variant="secondary" className="bg-green-500 text-white dark:bg-green-600">
                  <BadgeCheckIcon className="mr-1" />
                  {verificationResult.status}
                </Badge>
              ) : (
                <Badge variant="secondary" className="bg-red-500 text-white dark:bg-red-600">
                  <AlertCircleIcon className="mr-1" />
                  INVALID
                </Badge>
              )}
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label className="text-muted-foreground">Signatory</Label>
                <div>{verificationResult.signatory}</div>
              </div>
              <div>
                <Label className="text-muted-foreground">Algorithm</Label>
                <div>{verificationResult.algorithm}</div>
              </div>
              <div>
                <Label className="text-muted-foreground">Date/Time</Label>
                <div>{new Date(verificationResult.signedAt).toLocaleString()}</div>
              </div>
              <div>
                <Label className="text-muted-foreground">Verification Count</Label>
                <div>{verificationResult.verificationCount}</div>
              </div>
            </div>
            <div className="mt-4">
              <Label className="text-muted-foreground">Original Text</Label>
              <div className="mt-1 p-2 bg-gray-50 dark:bg-gray-800 rounded text-sm">
                {verificationResult.originalText}
              </div>
            </div>
          </div>
        </CardFooter>
      )}
      </Card>
  )
}