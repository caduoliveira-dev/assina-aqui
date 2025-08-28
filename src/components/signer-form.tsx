"use client"

import { useState } from "react"
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
  const [isSigned, setIsSigned] = useState(false)

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsSigned(true)
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
            <div className="grid gap-3">
              <Textarea 
                placeholder="Type here"
                required
              />
            </div>
            <div className="flex flex-col gap-3">
              <Button type="submit" className="w-full cursor-pointer">
                Sign
              </Button>
            </div>
          </div>
        </form>
      </CardContent>
      {isSigned && (
      <CardFooter>
        <div className="w-full">
          <Separator className="mb-4" />
          <CopyIdSigner />
        </div>
      </CardFooter>
      )}
    </Card>
  )
}